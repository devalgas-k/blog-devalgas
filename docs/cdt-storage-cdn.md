# CDT Storage + CDN pour Blog Devalgas

## Objectifs et contraintes

- Deux espaces de stockage distincts:
  - Public: servir images/assets statiques via CDN.
  - Privé: liens protégés, accessibles via URL signée (SAS).
- Limite par container: 5 GB stockés, trafic cible ≈ 10× (≈ 50 GB/mois).
- Intégration Spring Boot pour génération SAS et configuration des URLs publiques.
- Procédures d’ajout manuel et gestion des assets CSS/JS actuels.

## Architecture cible

- Un compte Storage (Standard_LRS), containers:
  - public-assets: accès public de type Blob pour permettre l’origine CDN.
  - private-links: accès privé (aucun accès anonyme).
- Un profil Azure CDN Standard from Microsoft:
  - Endpoint cdn devalgas: origine = public-assets.
  - Domaine personnalisé (optionnel): cdn.devalgas.net avec TLS.
- App Service continue de servir HTML/API. Les assets statiques proviennent du CDN.

## Dimensionnement et coûts

- Stockage: 5 GB Hot LRS ≈ faible coût mensuel.
- CDN egress Zone 1 (Amérique du Nord/Europe): ≈ 0,07–0,09 €/GB pour les premiers 10 TB/mois.
- Objectif: 50 GB/mois egress ≈ 3,5–4,5 €/mois. Opérations Storage marginales.

## Nommage recommandé

- Resource group: rg-devalgas-web
- Storage account: stdvlassets (global unique)
- Containers: public-assets, private-links
- CDN profile: cdn-devalgas
- CDN endpoint: cdn-devalgas-endpoint

## Provisionnement (exemple CLI)

```bash
az group create -n rg-devalgas-web -l canadacentral
az storage account create -g rg-devalgas-web -n stdvlassets -l canadacentral --sku Standard_LRS --kind StorageV2
az storage container create --account-name stdvlassets -n public-assets --public-access blob
az storage container create --account-name stdvlassets -n private-links
az cdn profile create -g rg-devalgas-web -n cdn-devalgas --sku Standard_Microsoft
az cdn endpoint create -g rg-devalgas-web -n cdn-devalgas-endpoint --profile-name cdn-devalgas \
  --origin stdvlassets.blob.core.windows.net --origin-host-header stdvlassets.blob.core.windows.net \
  --origin-path /public-assets --query-string-caching-behavior UseQueryString
```

## Provisionnement via Terraform (recommandé)

### Variables

```hcl
enable_storage_cdn = true
storage_account_name = "stdvlassets"
storage_public_container = "public-assets"
storage_private_container = "private-links"
cdn_profile_name = "cdn-devalgas"
cdn_endpoint_name = "cdn-devalgas-endpoint"
cdn_querystring_behavior = "UseQueryString"
```

### Ressources (extraits)

```hcl
resource "azurerm_storage_account" "assets" {
  name                      = var.storage_account_name
  resource_group_name       = azurerm_resource_group.rg.name
  location                  = azurerm_resource_group.rg.location
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  account_kind              = "StorageV2"
  https_traffic_only_enabled = true
  min_tls_version           = "TLS1_2"
  allow_nested_items_to_be_public = true
}

resource "azurerm_storage_container" "public_assets" {
  name                  = var.storage_public_container
  storage_account_name  = azurerm_storage_account.assets.name
  container_access_type = "blob"
}

resource "azurerm_storage_container" "private_links" {
  name                  = var.storage_private_container
  storage_account_name  = azurerm_storage_account.assets.name
  container_access_type = "private"
}

resource "azurerm_cdn_profile" "cdn" {
  name                = var.cdn_profile_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "Standard_Microsoft"
}

resource "azurerm_cdn_endpoint" "cdn" {
  name                = var.cdn_endpoint_name
  profile_name        = azurerm_cdn_profile.cdn.name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location

  is_http_allowed           = false
  is_https_allowed          = true
  is_compression_enabled    = true
  content_types_to_compress = ["text/plain","text/css","application/javascript","application/json","image/svg+xml","font/woff2"]
  querystring_caching_behaviour = var.cdn_querystring_behavior

  origin_host_header = format("%s.blob.core.windows.net", azurerm_storage_account.assets.name)
  origin_path        = "/${var.storage_public_container}"
  origin {
    name       = "storage-origin"
    host_name  = format("%s.blob.core.windows.net", azurerm_storage_account.assets.name)
    https_port = 443
  }
}
```

### App Settings (injection côté App Service)

```hcl
# merge(...) dans azurerm_linux_web_app.app
"CDN_BASE_URL"                    = "https://${var.cdn_endpoint_name}.azureedge.net"
"AZURE_STORAGE_ACCOUNT_NAME"      = var.storage_account_name
"AZURE_STORAGE_PUBLIC_CONTAINER"  = var.storage_public_container
"AZURE_STORAGE_PRIVATE_CONTAINER" = var.storage_private_container
```

### Commandes Terraform

```bash
cd terraform
terraform init -upgrade
terraform plan -var-file=./storage-cdn.tfvars
terraform apply -var-file=./storage-cdn.tfvars
```

### tfvars d’exemple (Canada Central)

```hcl
resource_group_name      = "rg-devalgas-web"
location                 = "Canada Central"
enable_storage_cdn       = true
storage_account_name     = "stdvlassets"
storage_public_container = "public-assets"
storage_private_container= "private-links"
cdn_profile_name         = "cdn-devalgas"
cdn_endpoint_name        = "cdn-devalgas-endpoint"
cdn_querystring_behavior = "UseQueryString"
```

## Option: clé Storage via Key Vault

- Créer un secret `storage-account-key` dans Key Vault avec la clé du compte.
- Ajouter un app setting référencé (si besoin côté Spring pour SAS):
  - `AZURE_STORAGE_ACCOUNT_KEY=@Microsoft.KeyVault(SecretUri=<KV Secret URI>)`
- Recommandation: privilégier l’authent Azure AD (Managed Identity) quand possible au lieu des clés.

## Configuration Storage

- Public-assets:
  - Public access level: Blob
  - Cache-Control sur blobs versionnés: `public, max-age=31536000, immutable`
  - Content-Type correct (image/webp, text/css, application/javascript)
- Private-links:
  - Public access: None
  - Accès via SAS court (ex. 5–15 min), IP optional, permissions lecture

## Configuration CDN

- Origine: stdvlassets.blob.core.windows.net/public-assets
- Compression: activée (gzip/br)
- Règles:
  - Forcer Cache-Control si absent
  - Autoriser CORS selon besoin (images, fonts)
- Domaine personnalisé: cdn.devalgas.net (CNAME vers endpoint), certificat géré

## Intégration Spring Boot

### Variables de configuration (application.yml)

```yaml
devalgas:
  cdn:
    base-url: ${CDN_BASE_URL:https://cdn-devalgas-endpoint.azureedge.net}
  storage:
    account-name: ${AZURE_STORAGE_ACCOUNT_NAME:stdvlassets}
    account-key: ${AZURE_STORAGE_ACCOUNT_KEY:}
    public-container: ${AZURE_STORAGE_PUBLIC_CONTAINER:public-assets}
    private-container: ${AZURE_STORAGE_PRIVATE_CONTAINER:private-links}
    sas-ttl-minutes: ${AZURE_STORAGE_SAS_TTL_MINUTES:10}
```

### Dépendance Maven

```xml
<dependency>
  <groupId>com.azure</groupId>
  <artifactId>azure-storage-blob</artifactId>
  <version>12.25.0</version>
</dependency>
```

### Service Java: URL publique CDN et SAS privé

```java
import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import com.azure.storage.common.StorageSharedKeyCredential;
import java.time.OffsetDateTime;

public class StorageLinkService {

  private final String cdnBaseUrl;
  private final String accountName;
  private final String accountKey;
  private final String publicContainer;
  private final String privateContainer;
  private final int sasTtlMinutes;

  public StorageLinkService(
    String cdnBaseUrl,
    String accountName,
    String accountKey,
    String publicContainer,
    String privateContainer,
    int sasTtlMinutes
  ) {
    this.cdnBaseUrl = cdnBaseUrl;
    this.accountName = accountName;
    this.accountKey = accountKey;
    this.publicContainer = publicContainer;
    this.privateContainer = privateContainer;
    this.sasTtlMinutes = sasTtlMinutes;
  }

  public String publicCdnUrl(String path) {
    return cdnBaseUrl + "/" + path;
  }

  public String privateSasUrl(String path) {
    StorageSharedKeyCredential cred = new StorageSharedKeyCredential(accountName, accountKey);
    BlobServiceClient svc = new BlobServiceClientBuilder()
      .endpoint("https://" + accountName + ".blob.core.windows.net")
      .credential(cred)
      .buildClient();
    BlobClient blob = svc.getBlobContainerClient(privateContainer).getBlobClient(path);
    BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);
    OffsetDateTime start = OffsetDateTime.now().minusMinutes(1);
    OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(sasTtlMinutes);
    BlobServiceSasSignatureValues v = new BlobServiceSasSignatureValues(expiry, perm).setStartTime(start);
    String sas = blob.generateSas(v);
    return blob.getBlobUrl() + "?" + sas;
  }
}

```

## Ajout manuel d’assets

### Via Azure CLI

```bash
# Upload batch vers public-assets (build Vite dist/assets)
az storage blob upload-batch --account-name stdvlassets -d public-assets -s ./dist/assets

# Définir Cache-Control pour assets versionnés
for f in $(find dist/assets -type f); do
  az storage blob update --account-name stdvlassets -c public-assets -n "$(basename "$f")" \
    --content-cache-control "public, max-age=31536000, immutable"
done
```

### Via Storage Explorer / Portail

- Glisser-déposer les fichiers vers public-assets.
- Vérifier Content-Type et Cache-Control.
- Pour private-links, utiliser l’outil SAS pour générer des URLs temporaires de test.

### Étape CI/CD (upload automatique)

- Après `npm run build`, exécuter:

```bash
az storage blob upload-batch --account-name "$AZURE_STORAGE_ACCOUNT_NAME" \
  -d "$AZURE_STORAGE_PUBLIC_CONTAINER" -s ./dist/assets

for f in $(find dist/assets -type f); do
  name=$(basename "$f")
  az storage blob update --account-name "$AZURE_STORAGE_ACCOUNT_NAME" \
    -c "$AZURE_STORAGE_PUBLIC_CONTAINER" -n "$name" \
    --content-cache-control "public, max-age=31536000, immutable"
done
```

- Variables nécessaires en CI:
  - `AZURE_STORAGE_ACCOUNT_NAME`, `AZURE_STORAGE_PUBLIC_CONTAINER`
  - Authent Azure CLI (OIDC ou SP) avec permissions de data plane sur le Storage.

### CI GitHub Actions (OIDC)

```yaml
name: Upload assets to Storage
on:
  workflow_dispatch:
jobs:
  upload-assets:
    runs-on: ubuntu-latest
    permissions:
      id-token: write
      contents: read
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: npm ci
      - run: npm run build
      - uses: azure/login@v2
        with:
          client-id: ${{ secrets.AZURE_CLIENT_ID }}
          tenant-id: ${{ secrets.AZURE_TENANT_ID }}
          subscription-id: ${{ secrets.AZURE_SUBSCRIPTION_ID }}
      - run: |
          az storage blob upload-batch --account-name "$AZURE_STORAGE_ACCOUNT_NAME" -d "$AZURE_STORAGE_PUBLIC_CONTAINER" -s ./dist/assets
          for f in $(find dist/assets -type f); do name=$(basename "$f"); az storage blob update --account-name "$AZURE_STORAGE_ACCOUNT_NAME" -c "$AZURE_STORAGE_PUBLIC_CONTAINER" -n "$name" --content-cache-control "public, max-age=31536000, immutable"; done
        env:
          AZURE_STORAGE_ACCOUNT_NAME: stdvlassets
          AZURE_STORAGE_PUBLIC_CONTAINER: public-assets
```

### RBAC requis

```bash
ACCOUNT_ID=$(az storage account show -g rg-devalgas-web -n stdvlassets --query id -o tsv)
SP_OBJECT_ID=$(az ad sp show --id "$AZURE_CLIENT_ID" --query id -o tsv)
az role assignment create --assignee "$SP_OBJECT_ID" --role "Storage Blob Data Contributor" --scope "$ACCOUNT_ID"
```

### Alternative Service Principal

```bash
az login --service-principal -u "$AZURE_CLIENT_ID" -p "$AZURE_CLIENT_SECRET" --tenant "$AZURE_TENANT_ID"
```

## Gestion des assets CSS/JS actuels (Vite + Vue)

### Vite (base CDN)

```ts
// vite.config.ts
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  base: process.env.VITE_CDN_BASE_URL || '/',
  build: { manifest: true },
});
```

### Upload et références

- Construire le front: `npm run build`.
- Uploader `dist/assets/` vers `public-assets`.
- Servir `index.html` depuis l’App Service; les assets référencés utilisent `base` vers le CDN.
- Versionner les assets (hash filename) pour permettre `immutable`.

## Sécurité et conformité

- Désactiver l’accès public au niveau compte; l’activer uniquement sur `public-assets` (Blob).
- Private-links strictement via SAS court et permissions lecture.
- Activer HTTPS seulement sur CDN et Storage, CORS minimal.
- Option: référer le CDN par domaine personnalisé pour éviter hotlinking.

## Budgets et alertes

- Créer un Budget Azure:
  - Stockage: alerte à 5 GB used.
  - CDN: alerte à 50 GB/mois egress Zone 1.
- Alertes métriques: cache hit ratio < 90%, 4xx/5xx sur CDN.

## Checklist d’implémentation

- Terraform: activer `enable_storage_cdn=true`, appliquer avec tfvars.
- Mettre `VITE_CDN_BASE_URL` (ou `base`) et reconstruire le front.
- Uploader `dist/assets` vers `public-assets` avec headers cache.
- Utiliser `CDN_BASE_URL` pour référencer les assets côté front.
- Générer des SAS pour `private-links` côté Spring (service fourni).
- Mettre en place budgets et alertes (5 GB/50 GB/mois).

## Rollout et rollback

- Phase 1: activer CDN, basculer `base` Vite, uploader assets.
- Phase 2: mesurer perfs et coûts 2–4 semaines.
- Rollback: remettre `base` à `/`, supprimer endpoint CDN si non retenu.

## Bonnes pratiques

- Formats images modernes (WebP/AVIF).
- Headers longs pour assets versionnés, courts pour HTML.
- Minimiser purges CDN; invalider uniquement sur nouvelles versions.

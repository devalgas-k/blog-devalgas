Commandes utiles Azure

- URL du secret Key Vault (nécessite un jeton Bearer):

https://kv-devalgas-blog.vault.azure.net/secrets/smtp-password/12b5a5ae049340e6b932959ca36df68b

- Lister les App Settings de la Web App:

```bash
az webapp config appsettings list --name app-springboot-blog-devalgas --resource-group rg-devalgas-springboot-azure
```

- Suivre les logs applicatifs en temps réel:

```bash
az webapp log tail -g rg-devalgas-springboot-azure -n app-springboot-blog-devalgas
```

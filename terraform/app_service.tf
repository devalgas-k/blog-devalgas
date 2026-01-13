resource "azurerm_service_plan" "asp" {
  name                = "asp-springboot"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  os_type             = "Linux"
  sku_name            = "B1" # 1.75 GB RAM, ~13$/month (Base)
}

resource "azurerm_linux_web_app" "app" {
  name                = var.app_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_service_plan.asp.location
  service_plan_id     = azurerm_service_plan.asp.id

  site_config {
    application_stack {
      java_server         = "JAVA"
      java_server_version = "17"
      java_version        = "17"
    }
  }

  app_settings = merge(
    {
      "SPRING_PROFILES_ACTIVE" = "prod"

      # Database
      "SPRING_DATASOURCE_URL"      = "jdbc:postgresql://${azurerm_postgresql_flexible_server.db.fqdn}:5432/${azurerm_postgresql_flexible_server_database.app_db.name}?sslmode=require"
      "SPRING_DATASOURCE_USERNAME" = var.admin_username
      "SPRING_DATASOURCE_PASSWORD" = var.enable_keyvault ? "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.db_password[0].id})" : var.db_password

      # Security
      "JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET" = var.enable_keyvault ? "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.jwt_secret[0].id})" : var.jwt_secret

      # App Configs (from .env.example)
      "RECAPTCHA_SITE_KEY" = var.recaptcha_site_key
      "LINKEDIN_URL"       = var.social_urls["linkedin"]
      "TWITTER_URL"        = var.social_urls["twitter"]
      "GITHUB_URL"         = var.social_urls["github"]
      "MEDIUM_URL"         = var.social_urls["medium"]
      "WHATSAPP_URL"       = var.social_urls["whatsapp"]
      "CONTACT_EMAIL"      = var.contact_info["contact_email"]
      "CONTACT_PHONE"      = var.contact_info["contact_phone"]
    },
    var.enable_email_service ? {
      # Mail (optionnel)
      "SPRING_MAIL_HOST"                                 = var.mail_host
      "SPRING_MAIL_PORT"                                 = var.mail_port
      "SPRING_MAIL_USERNAME"                             = var.mail_username
      "SPRING_MAIL_PASSWORD"                             = var.enable_keyvault ? "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.smtp_password[0].id})" : var.mail_password
      "SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH"            = "true"
      "SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE" = "true"
      "JHIPSTER_MAIL_FROM"                               = var.mail_from
    } : {}
  )

  identity {
    type = "SystemAssigned"
  }
}

# Custom Domain Bindings for multiple domains
resource "azurerm_app_service_custom_hostname_binding" "domain_binding" {
  for_each            = var.enable_custom_domains ? toset(var.domains) : []
  hostname            = each.value
  app_service_name    = azurerm_linux_web_app.app.name
  resource_group_name = azurerm_resource_group.rg.name
}

# Managed Certificates (SSL) for each domain
resource "azurerm_app_service_managed_certificate" "cert" {
  for_each                   = var.enable_custom_domains ? toset(var.domains) : []
  custom_hostname_binding_id = azurerm_app_service_custom_hostname_binding.domain_binding[each.value].id
}

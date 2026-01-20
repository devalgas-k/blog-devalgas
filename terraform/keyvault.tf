data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "kv" {
  count                       = var.enable_keyvault ? 1 : 0
  name                        = var.key_vault_name
  location                    = azurerm_resource_group.rg.location
  resource_group_name         = azurerm_resource_group.rg.name
  enabled_for_disk_encryption = true
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days  = 7
  purge_protection_enabled    = false
  enable_rbac_authorization   = var.enable_keyvault_rbac

  sku_name = "standard"

  lifecycle {
    ignore_changes = [enable_rbac_authorization]
  }

  dynamic "access_policy" {
    for_each = var.enable_keyvault_rbac ? [] : [1]
    content {
      tenant_id          = data.azurerm_client_config.current.tenant_id
      object_id          = data.azurerm_client_config.current.object_id
      secret_permissions = ["Get", "List", "Set", "Delete", "Purge", "Recover"]
    }
  }
}

resource "azurerm_key_vault_access_policy" "app" {
  count        = var.enable_keyvault && !var.enable_keyvault_rbac ? 1 : 0
  key_vault_id = azurerm_key_vault.kv[0].id
  tenant_id    = azurerm_linux_web_app.app.identity[0].tenant_id
  object_id    = azurerm_linux_web_app.app.identity[0].principal_id

  secret_permissions = var.enable_app_list_secrets ? ["Get", "List"] : ["Get"]
}

resource "azurerm_key_vault_access_policy" "admin_user" {
  count        = var.enable_keyvault && !var.enable_keyvault_rbac && var.enable_admin_user_access && var.admin_user_object_id != "" ? 1 : 0
  key_vault_id = azurerm_key_vault.kv[0].id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = var.admin_user_object_id

  secret_permissions = ["Get", "List", "Set"]
}

resource "azurerm_role_assignment" "kv_current_client_secrets_officer" {
  count                = var.enable_keyvault && var.enable_keyvault_rbac ? 1 : 0
  scope                = azurerm_key_vault.kv[0].id
  role_definition_name = "Key Vault Secrets Officer"
  principal_id         = data.azurerm_client_config.current.object_id
}

resource "azurerm_role_assignment" "kv_app_secrets_user" {
  count                = var.enable_keyvault && var.enable_keyvault_rbac ? 1 : 0
  scope                = azurerm_key_vault.kv[0].id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_linux_web_app.app.identity[0].principal_id
}

resource "azurerm_role_assignment" "kv_admin_secrets_officer" {
  count                = var.enable_keyvault && var.enable_keyvault_rbac && var.enable_admin_user_access && var.admin_user_object_id != "" ? 1 : 0
  scope                = azurerm_key_vault.kv[0].id
  role_definition_name = "Key Vault Secrets Officer"
  principal_id         = var.admin_user_object_id
}

resource "azurerm_key_vault_secret" "db_password" {
  count        = var.enable_keyvault ? 1 : 0
  name         = "db-password"
  value        = var.db_password
  key_vault_id = azurerm_key_vault.kv[0].id
  depends_on   = [azurerm_role_assignment.kv_current_client_secrets_officer]
}

resource "azurerm_key_vault_secret" "jwt_secret" {
  count        = var.enable_keyvault ? 1 : 0
  name         = "jwt-secret"
  value        = var.jwt_secret
  key_vault_id = azurerm_key_vault.kv[0].id
  depends_on   = [azurerm_role_assignment.kv_current_client_secrets_officer]
}

resource "azurerm_key_vault_secret" "smtp_password" {
  count        = var.enable_email_service && var.enable_keyvault && length(var.mail_password) > 0 ? 1 : 0
  name         = "smtp-password"
  value        = var.mail_password
  key_vault_id = azurerm_key_vault.kv[0].id
  depends_on   = [azurerm_role_assignment.kv_current_client_secrets_officer]
}

resource "azurerm_key_vault_secret" "fallback_smtp_password" {
  count        = var.enable_email_service && var.enable_keyvault && var.fallback_mail_enabled && length(var.fallback_mail_password) > 0 ? 1 : 0
  name         = "fallback-smtp-password"
  value        = var.fallback_mail_password
  key_vault_id = azurerm_key_vault.kv[0].id
  depends_on   = [azurerm_role_assignment.kv_current_client_secrets_officer]
}

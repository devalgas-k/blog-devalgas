resource "azurerm_security_center_subscription_pricing" "app_services" {
  count         = var.enable_defender_pricing ? 1 : 0
  resource_type = "AppServices"
  tier          = var.defender_tier_app_services
}

resource "azurerm_security_center_subscription_pricing" "open_source_db" {
  count         = var.enable_defender_pricing ? 1 : 0
  resource_type = "OpenSourceRelationalDatabases"
  tier          = var.defender_tier_open_source_db
}

resource "azurerm_security_center_subscription_pricing" "key_vaults" {
  count         = var.enable_defender_pricing ? 1 : 0
  resource_type = "KeyVaults"
  tier          = var.defender_tier_key_vaults
}

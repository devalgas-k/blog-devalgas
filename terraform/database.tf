resource "azurerm_postgresql_flexible_server" "db" {
  name                   = var.db_name
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "14"
  administrator_login    = var.admin_username
  administrator_password = var.db_password
  storage_mb             = 65536             # 64GB to support P6 tier
  sku_name               = "B_Standard_B1ms" # Free tier eligible in Canada Central
  storage_tier           = "P6"              # Performance level P6 (240 IOPS)

  lifecycle {
    ignore_changes = [
      zone
    ]
  }
}

resource "azurerm_postgresql_flexible_server_database" "app_db" {
  name      = "appdb"
  server_id = azurerm_postgresql_flexible_server.db.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

# Allow Azure services to access the DB (necessary for App Service)
resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure_services" {
  name             = "allow-azure-services"
  server_id        = azurerm_postgresql_flexible_server.db.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

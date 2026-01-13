resource "azurerm_communication_service" "email" {
  count               = var.enable_email_service ? 1 : 0
  name                = "acs-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  data_location       = "United States"
}

resource "azurerm_email_communication_service" "email_service" {
  count               = var.enable_email_service ? 1 : 0
  name                = "email-${var.app_name}"
  resource_group_name = azurerm_resource_group.rg.name
  data_location       = "United States"
}

# Utilisation d'un domaine géré par Azure pour commencer (gratuit et rapide)
resource "azurerm_email_communication_service_domain" "azure_managed_domain" {
  count             = var.enable_email_service ? 1 : 0
  name              = "AzureManagedDomain"
  email_service_id  = azurerm_email_communication_service.email_service[0].id
  domain_management = "AzureManaged"
}

# Liaison entre le service de communication et le domaine
resource "azurerm_communication_service_email_domain_association" "assoc" {
  count                    = var.enable_email_service ? 1 : 0
  communication_service_id = azurerm_communication_service.email[0].id
  email_service_domain_id  = azurerm_email_communication_service_domain.azure_managed_domain[0].id
}

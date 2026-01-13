resource "azurerm_monitor_action_group" "main" {
  name                = "ag-springboot-alerts"
  resource_group_name = azurerm_resource_group.rg.name
  short_name          = "sb-alerts"

  email_receiver {
    name                    = "sendtoadmin"
    email_address           = var.alert_email
    use_common_alert_schema = true
  }

  # Note: The Logic App/Webhook URL would be added here to trigger the auto-scaling
  # logic_app_receiver {
  #   resource_id = azurerm_logic_app_workflow.autoscale.id
  #   callback_url = azurerm_logic_app_workflow.autoscale.access_endpoint
  # }
}

resource "azurerm_monitor_metric_alert" "high_memory" {
  name                = "alert-high-memory"
  resource_group_name = azurerm_resource_group.rg.name
  scopes              = [azurerm_linux_web_app.app.id]
  description         = "Triggered when AverageMemoryWorkingSet > 1.5 GB. Action: Scale Up to B2."
  severity            = 1
  frequency           = "PT1M"
  window_size         = "PT5M"

  criteria {
    metric_namespace = "Microsoft.Web/sites"
    metric_name      = "AverageMemoryWorkingSet"
    aggregation      = "Average"
    operator         = "GreaterThan"
    threshold        = 1610612736
  }

  action {
    action_group_id = azurerm_monitor_action_group.main.id
  }
}

resource "azurerm_monitor_metric_alert" "low_memory" {
  name                = "alert-low-memory"
  resource_group_name = azurerm_resource_group.rg.name
  scopes              = [azurerm_linux_web_app.app.id]
  description         = "Triggered when AverageMemoryWorkingSet < 1.2 GB. Action: Scale Down to B1."
  severity            = 3
  frequency           = "PT5M"
  window_size         = "PT15M"

  criteria {
    metric_namespace = "Microsoft.Web/sites"
    metric_name      = "AverageMemoryWorkingSet"
    aggregation      = "Average"
    operator         = "LessThan"
    threshold        = 1288490188
  }

  action {
    action_group_id = azurerm_monitor_action_group.main.id
  }
}

# DB Alert: Scale Up to P10 when CPU > 90%
resource "azurerm_monitor_metric_alert" "db_high_cpu" {
  name                = "alert-db-high-cpu"
  resource_group_name = azurerm_resource_group.rg.name
  scopes              = [azurerm_postgresql_flexible_server.db.id]
  description         = "Triggered when DB CPU > 90%. Action: Scale Up to P10."
  severity            = 1
  frequency           = "PT1M"
  window_size         = "PT5M"

  criteria {
    metric_namespace = "Microsoft.DBforPostgreSQL/flexibleServers"
    metric_name      = "cpu_percent"
    aggregation      = "Average"
    operator         = "GreaterThan"
    threshold        = 90
  }

  action {
    action_group_id = azurerm_monitor_action_group.main.id
  }
}

# DB Alert: Scale Down to P6 when CPU < 80%
resource "azurerm_monitor_metric_alert" "db_low_cpu" {
  name                = "alert-db-low-cpu"
  resource_group_name = azurerm_resource_group.rg.name
  scopes              = [azurerm_postgresql_flexible_server.db.id]
  description         = "Triggered when DB CPU < 80%. Action: Scale Down to P6."
  severity            = 3
  frequency           = "PT5M"
  window_size         = "PT15M"

  criteria {
    metric_namespace = "Microsoft.DBforPostgreSQL/flexibleServers"
    metric_name      = "cpu_percent"
    aggregation      = "Average"
    operator         = "LessThan"
    threshold        = 80
  }

  action {
    action_group_id = azurerm_monitor_action_group.main.id
  }
}

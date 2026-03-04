resource "azurerm_cdn_frontdoor_profile" "cdn" {
  count               = var.enable_cdn ? 1 : 0
  name                = var.cdn_profile_name
  resource_group_name = azurerm_resource_group.rg.name
  sku_name            = "Standard_AzureFrontDoor"
}

resource "azurerm_cdn_frontdoor_endpoint" "cdn" {
  count                    = var.enable_cdn ? 1 : 0
  name                     = var.cdn_endpoint_name
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.cdn[0].id
  enabled                  = true
}

resource "azurerm_cdn_frontdoor_origin_group" "app" {
  count                    = var.enable_cdn ? 1 : 0
  name                     = "origin-group-app"
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.cdn[0].id
  load_balancing {
    additional_latency_in_milliseconds = 0
    sample_size                        = 4
    successful_samples_required        = 3
  }
  health_probe {
    path                = "/"
    protocol            = "Https"
    request_type        = "HEAD"
    interval_in_seconds = 120
  }
}

resource "azurerm_cdn_frontdoor_origin" "app" {
  count                          = var.enable_cdn ? 1 : 0
  name                           = "origin-app"
  cdn_frontdoor_origin_group_id  = azurerm_cdn_frontdoor_origin_group.app[0].id
  enabled                        = true
  certificate_name_check_enabled = false
  host_name                      = var.cdn_origin_hostname
  origin_host_header             = var.cdn_origin_hostname
  http_port                      = 80
  https_port                     = 443
  priority                       = 1
  weight                         = 1000
}

resource "azurerm_cdn_frontdoor_route" "route_all" {
  count                         = var.enable_cdn ? 1 : 0
  name                          = "route-all"
  cdn_frontdoor_endpoint_id     = azurerm_cdn_frontdoor_endpoint.cdn[0].id
  cdn_frontdoor_origin_group_id = azurerm_cdn_frontdoor_origin_group.app[0].id
  cdn_frontdoor_origin_ids      = [azurerm_cdn_frontdoor_origin.app[0].id]
  enabled                       = true
  forwarding_protocol           = "HttpsOnly"
  https_redirect_enabled        = true
  patterns_to_match             = ["/*"]
  supported_protocols           = ["Http", "Https"]
  link_to_default_domain        = true

  cache {
    query_string_caching_behavior = var.cdn_querystring_behavior == "UseQueryString" ? "UseQueryString" : "IgnoreQueryString"
    compression_enabled           = true
    content_types_to_compress     = ["text/plain", "text/css", "application/javascript", "application/json", "image/svg+xml", "text/html"]
  }
}

resource "azurerm_consumption_budget_subscription" "cdn_monthly" {
  count           = var.enable_cdn ? 1 : 0
  name            = "budget-cdn"
  subscription_id = "/subscriptions/${data.azurerm_client_config.current.subscription_id}"
  amount          = var.cdn_monthly_budget_eur
  time_grain      = "Monthly"
  time_period {
    start_date = can(regex("T[0-9]{2}:", var.cdn_budget_start_date)) ? var.cdn_budget_start_date : "${var.cdn_budget_start_date}T00:00:00Z"
    end_date   = "2030-12-31T00:00:00Z"
  }
  notification {
    threshold      = 50
    operator       = "GreaterThan"
    threshold_type = "Actual"
    contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email]
    enabled        = true
  }
  notification {
    threshold      = 80
    operator       = "GreaterThan"
    threshold_type = "Actual"
    contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email]
    enabled        = true
  }
  notification {
    threshold      = 100
    operator       = "GreaterThanOrEqualTo"
    threshold_type = "Actual"
    contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email]
    enabled        = true
  }
}

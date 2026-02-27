resource "github_actions_variable" "adsense_enabled" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_ENABLED"
  value         = var.adsense_enabled ? "true" : "false"
}

resource "github_actions_variable" "adsense_client" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_CLIENT"
  value         = var.adsense_client
}

resource "github_actions_variable" "adsense_slot_top" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_SLOT_TOP"
  value         = var.adsense_slot_top
}

resource "github_actions_variable" "adsense_slot_sidebar_left" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_SLOT_SIDEBAR_LEFT"
  value         = var.adsense_slot_sidebar_left
}

resource "github_actions_variable" "adsense_slot_sidebar_right" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_SLOT_SIDEBAR_RIGHT"
  value         = var.adsense_slot_sidebar_right
}

resource "github_actions_variable" "adsense_slot_footer" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "ADSENSE_SLOT_FOOTER"
  value         = var.adsense_slot_footer
}

resource "github_actions_variable" "recaptcha_site_key" {
  count         = var.manage_github_vars ? 1 : 0
  repository    = var.repository
  variable_name = "RECAPTCHA_SITE_KEY"
  value         = var.recaptcha_site_key
}

resource "github_actions_secret" "recaptcha_secret" {
  count           = var.manage_github_vars && var.recaptcha_secret != "" ? 1 : 0
  repository      = var.repository
  secret_name     = "RECAPTCHA_SECRET"
  plaintext_value = var.recaptcha_secret
}

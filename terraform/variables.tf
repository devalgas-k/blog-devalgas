variable "resource_group_name" {
  type    = string
  default = "rg-devalgas-springboot-azure"
}

variable "location" {
  type    = string
  default = "Canada Central"
}

variable "domains" {
  type    = list(string)
  default = ["devalgas.com", "devalgas.fr"]
}

variable "app_name" {
  type    = string
  default = "app-springboot-blog-devalgas"
}

variable "db_name" {
  type    = string
  default = "db-postgres-blog-devalgas"
}

variable "admin_username" {
  type    = string
  default = "devalgas"
}

variable "alert_email" {
  type    = string
  default = "kamgadevalgas@gmail.com"
}

# --- Application Secrets & Configs ---

variable "jwt_secret" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "recaptcha_site_key" {
  type    = string
  default = "6LewAUwsAAAAAOVXC6a37SgGw4TOQa4T9JUo6wcK"
}

variable "recaptcha_secret" {
  type      = string
  sensitive = true
  default   = "6LewAUwsAAAAABLATqSXUf-MHblYcvuXwKZlLWCJ"
}

variable "social_urls" {
  type = map(string)
  default = {
    linkedin = "https://www.linkedin.com/in/devalgas-kamga/"
    twitter  = "https://x.com/devalgas1/"
    github   = "https://github.com/devalgas-k/"
    medium   = "https://medium.com/@kamgadevalgas"
    whatsapp = "https://wa.me/23055040199"
  }
}

variable "contact_info" {
  type = map(string)
  default = {
    contact_email = "contact@devalgas.net"
    contact_phone = "tel:+23055040199"
  }
}

variable "enable_email_service" {
  type    = bool
  default = true
}

variable "use_azure_communication_services" {
  type    = bool
  default = false
}

variable "fallback_mail_enabled" {
  type    = bool
  default = false
}

variable "enable_custom_domains" {
  type    = bool
  default = false
}

variable "mail_host" {
  type    = string
  default = ""
}

variable "mail_port" {
  type    = string
  default = "587"
}

variable "mail_username" {
  type    = string
  default = ""
}

variable "mail_password" {
  type      = string
  sensitive = true
  default   = ""
}

variable "mail_from" {
  type    = string
  default = "donotreply@example.com"
}

variable "mail_from_contact" {
  type    = string
  default = "contact@devalgas.net"
}

variable "mail_base_url" {
  type    = string
  default = ""
}
variable "fallback_mail_host" {
  type    = string
  default = ""
}

variable "fallback_mail_port" {
  type    = string
  default = "587"
}

variable "fallback_mail_username" {
  type    = string
  default = ""
}

variable "fallback_mail_password" {
  type      = string
  sensitive = true
  default   = ""
}

variable "use_oidc" {
  type    = bool
  default = true
}
variable "enable_keyvault" {
  type    = bool
  default = false
}
variable "enable_keyvault_rbac" {
  type    = bool
  default = false
  validation {
    condition     = var.enable_keyvault_rbac ? var.enable_keyvault : true
    error_message = "enable_keyvault must be true when enable_keyvault_rbac is true."
  }
}
variable "key_vault_name" {
  type    = string
  default = "kv-devalgas-blog"
  validation {
    condition     = length(var.key_vault_name) >= 3 && length(var.key_vault_name) <= 24 && can(regex("^([a-z][a-z0-9-]*[a-z0-9])$", var.key_vault_name)) && !can(regex("--", var.key_vault_name))
    error_message = "key_vault_name must be 3-24 chars, lowercase alphanumerics and hyphens, start with a letter, end with a letter/digit, and have no consecutive hyphens."
  }
}
variable "enable_app_list_secrets" {
  type    = bool
  default = true
}
variable "enable_admin_user_access" {
  type    = bool
  default = true
}
variable "admin_user_object_id" {
  type    = string
  default = ""
}

variable "enable_defender_pricing" {
  type    = bool
  default = true
}

variable "defender_tier_app_services" {
  type    = string
  default = "Free"
  validation {
    condition     = contains(["Free", "Standard"], var.defender_tier_app_services)
    error_message = "defender_tier_app_services must be either 'Free' or 'Standard'."
  }
}

variable "defender_tier_open_source_db" {
  type    = string
  default = "Free"
  validation {
    condition     = contains(["Free", "Standard"], var.defender_tier_open_source_db)
    error_message = "defender_tier_open_source_db must be either 'Free' or 'Standard'."
  }
}

variable "defender_tier_key_vaults" {
  type    = string
  default = "Free"
  validation {
    condition     = contains(["Free", "Standard"], var.defender_tier_key_vaults)
    error_message = "defender_tier_key_vaults must be either 'Free' or 'Standard'."
  }
}

# --- GitHub Actions variables management ---
variable "github_token" {
  type      = string
  sensitive = true
  default   = ""
}

variable "github_owner" {
  type    = string
  default = "devalgas"
}

variable "repository" {
  type    = string
  default = "blog-devalgas"
}

variable "manage_github_vars" {
  type    = bool
  default = false
}

variable "adsense_enabled" {
  type    = bool
  default = true
}

variable "adsense_client" {
  type    = string
  default = "ca-pub-6181972205565553"
}

variable "adsense_slot_top" {
  type    = string
  default = "0000000001"
}

variable "adsense_slot_sidebar_left" {
  type    = string
  default = "0000000002"
}

variable "adsense_slot_sidebar_right" {
  type    = string
  default = "0000000003"
}

variable "adsense_slot_footer" {
  type    = string
  default = "0000000004"
}

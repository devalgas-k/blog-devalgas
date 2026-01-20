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
  default = ""
}

variable "recaptcha_secret" {
  type      = string
  sensitive = true
  default   = ""
}

variable "social_urls" {
  type = map(string)
  default = {
    linkedin = ""
    twitter  = ""
    github   = ""
    medium   = ""
    whatsapp = ""
  }
}

variable "contact_info" {
  type = map(string)
  default = {
    contact_email = ""
    contact_phone = ""
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

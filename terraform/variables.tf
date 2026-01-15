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
    whatsapp = "https://wa.me/237699520388"
  }
}

variable "contact_info" {
  type = map(string)
  default = {
    contact_email = "kamgadevalga@icloud.com"
    contact_phone = "tel:+23055040199"
  }
}

variable "enable_email_service" {
  type    = bool
  default = true
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
  default = true
}
variable "enable_keyvault_rbac" {
  type    = bool
  default = true
  validation {
    condition     = var.enable_keyvault_rbac == false || var.enable_keyvault == true
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

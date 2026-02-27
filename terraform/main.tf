terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
    github = {
      source  = "integrations/github"
      version = "~> 6.0"
    }
  }
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy = true
    }
  }
  use_oidc                   = var.use_oidc
  skip_provider_registration = true
}

provider "github" {
  token = var.github_token
  owner = var.github_owner
}

resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

<template>
  <b-navbar data-cy="navbar" toggleable="md" type="dark" class="bg-primary py-0">
    <b-navbar-brand class="logo" b-link to="/">
      <title-app class="text-dark" style="margin-top: -0.1em"></title-app>
    </b-navbar-brand>
    <b-navbar-toggle
      right
      class="jh-navbar-toggler d-lg-none"
      href="javascript:void(0);"
      data-toggle="collapse"
      target="header-tabs"
      aria-expanded="false"
      aria-label="Toggle navigation"
    >
      <font-awesome-icon icon="bars" />
    </b-navbar-toggle>

    <b-collapse is-nav id="header-tabs">
      <b-navbar-nav class="ml-auto row pl-lg-5">
        <b-nav-item to="/" exact v-if="!authenticated">
          <span>
            <font-awesome-icon icon="home" />
            <span v-text="t$('global.menu.home')"></span>
          </span>
        </b-nav-item>
      </b-navbar-nav>
    </b-collapse>

    <b-collapse is-nav id="header-tabs">
      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown right id="entity-menu" v-if="authenticated" active-class="active" class="pointer" data-cy="entity">
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="th-list" />
              <span class="no-bold" v-text="t$('global.menu.entities.main')"></span>
            </span>
          </template>
          <entities-menu></entities-menu>
          <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
        </b-nav-item-dropdown>
        <b-nav-item-dropdown
          right
          id="admin-menu"
          v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated"
          :class="{ 'router-link-active': subIsActive('/admin') }"
          active-class="active"
          class="pointer"
          data-cy="adminMenu"
        >
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="users-cog" />
              <span class="no-bold" v-text="t$('global.menu.admin.main')"></span>
            </span>
          </template>
          <b-dropdown-item to="/admin/user-management" active-class="active">
            <font-awesome-icon icon="users" />
            <span v-text="t$('global.menu.admin.userManagement')"></span>
          </b-dropdown-item>
          <b-dropdown-item to="/admin/tracker" active-class="active">
            <font-awesome-icon icon="eye" />
            <span v-text="t$('global.menu.admin.tracker')"></span>
          </b-dropdown-item>
          <b-dropdown-item to="/admin/metrics" active-class="active">
            <font-awesome-icon icon="tachometer-alt" />
            <span v-text="t$('global.menu.admin.metrics')"></span>
          </b-dropdown-item>
          <b-dropdown-item to="/admin/health" active-class="active">
            <font-awesome-icon icon="heart" />
            <span v-text="t$('global.menu.admin.health')"></span>
          </b-dropdown-item>
          <b-dropdown-item to="/admin/configuration" active-class="active">
            <font-awesome-icon icon="cogs" />
            <span v-text="t$('global.menu.admin.configuration')"></span>
          </b-dropdown-item>
          <b-dropdown-item to="/admin/logs" active-class="active">
            <font-awesome-icon icon="tasks" />
            <span v-text="t$('global.menu.admin.logs')"></span>
          </b-dropdown-item>
          <b-dropdown-item v-if="openAPIEnabled" to="/admin/docs" active-class="active">
            <font-awesome-icon icon="book" />
            <span v-text="t$('global.menu.admin.apidocs')"></span>
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

      <b-navbar-nav class="ml-auto inline-mobile-nav">
        <b-nav-item-dropdown id="languagesnavBarDropdown" left v-if="languages && Object.keys(languages).length > 1">
          <template #button-content>
            <font-awesome-icon icon="earth-africa" />
          </template>
          <b-dropdown-item
            v-for="(value, key) in languages"
            :key="`lang-${key}`"
            @click="changeLanguage(key)"
            :class="{ active: isActiveLanguage(key) }"
          >
            <img alt="flag" src="/content/images/flag_placeholder.png" :class="`flag flag-${value.code}`" style="width: 24px" />
            {{ value.name }}
          </b-dropdown-item>
        </b-nav-item-dropdown>

        <b-nav-item-dropdown id="themesnavBarDropdown" class="mx-1 px-0" right v-if="themes && Object.keys(themes).length > 1">
          <template #button-content>
            <font-awesome-icon icon="circle-half-stroke" />
          </template>
          <b-dropdown-item
            v-for="(value, key) in themes"
            :key="`theme-${key}`"
            @click="changeTheme(key)"
            :class="{ active: isActiveTheme(key) }"
          >
            <font-awesome-icon :icon="value.icon" :class="['theme-icon', 'theme-icon-' + key]" />

            {{ t$(value.nameKey) }}
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown
          right
          href="javascript:void(0);"
          id="account-menu"
          :class="{ 'router-link-active': subIsActive('/account') }"
          active-class="active"
          class="pointer"
          data-cy="accountMenu"
        >
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="user" />
              <span class="no-bold" v-text="t$('global.menu.account.main')"></span>
            </span>
          </template>
          <b-dropdown-item data-cy="settings" to="/account/settings" v-if="authenticated" active-class="active">
            <font-awesome-icon icon="wrench" />
            <span v-text="t$('global.menu.account.settings')"></span>
          </b-dropdown-item>
          <b-dropdown-item data-cy="passwordItem" to="/account/password" v-if="authenticated" active-class="active">
            <font-awesome-icon icon="lock" />
            <span v-text="t$('global.menu.account.password')"></span>
          </b-dropdown-item>
          <b-dropdown-item data-cy="logout" v-if="authenticated" @click="logout()" id="logout" active-class="active">
            <font-awesome-icon icon="sign-out-alt" />
            <span v-text="t$('global.menu.account.logout')"></span>
          </b-dropdown-item>
          <b-dropdown-item data-cy="login" v-if="!authenticated" @click="showLogin()" id="login" active-class="active">
            <font-awesome-icon icon="sign-in-alt" />
            <span v-text="t$('global.menu.account.login')"></span>
          </b-dropdown-item>
          <b-dropdown-item data-cy="register" to="/register" id="register" v-if="!authenticated" active-class="active">
            <font-awesome-icon icon="user-plus" />
            <span v-text="t$('global.menu.account.register')"></span>
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts" src="./headers-v1.component.ts"></script>

<style scoped>
.navbar-brand {
  padding: 0.3rem 0.5rem calc(15px - 0.75rem) !important;
}

@media screen and (min-width: 768px) {
  .jh-navbar-toggler {
    display: none;
  }
}

@media screen and (min-width: 768px) and (max-width: 1150px) {
  span span {
    display: none;
  }
}

.navbar-title {
  display: inline-block;
}

/* ==========================================================================
    Logo styles
    ========================================================================== */
.navbar-brand.logo {
  padding: 0 7px;
}

.logo .logo-img {
  height: 45px;
  display: inline-block;
  vertical-align: middle;
  width: 45px;
}

.logo-img {
  height: 100%;
  background: url('/content/images/logo-app.png') no-repeat center center;
  background-size: contain;
  width: 100%;
  filter: drop-shadow(0 0 0.05rem white);
  margin: 0 5px;
}

span.flag {
  width: 44px;
  height: 30px;
  display: inline-block;
}
img.flag {
  width: 30px;
}
.flag {
  background: url('/content/images/flags_responsive.png') no-repeat;
  background-size: 100%;
  vertical-align: middle;
}
.flag-fr {
  background-position: 0 29.752066%;
}
.flag-uk {
  background-position: 0 92.561983%;
}
.theme-icon-light {
  color: #b58900;
}
.theme-icon-dark {
  color: #000000;
}

@media screen and (max-width: 767px) {
  .inline-mobile-nav {
    display: flex;
    flex-direction: row !important;
    align-items: center;
    width: 100%;
    justify-content: flex-start;
  }
  .inline-mobile-nav > li.nav-item {
    margin-right: 0.25rem;
  }
}
</style>

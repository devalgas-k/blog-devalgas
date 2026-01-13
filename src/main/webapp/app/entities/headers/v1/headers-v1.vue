<template>
  <b-navbar data-cy="navbar" toggleable="md" type="dark" class="headers-v1 bg-primary py-0">
    <b-navbar-brand class="headers-v1__brand headers-v1__logo" b-link to="/">
      <title-app class="text-dark headers-v1__title"></title-app>
    </b-navbar-brand>
    <b-navbar-toggle
      right
      class="d-lg-none headers-v1__toggler"
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
        <b-nav-item :to="{ path: '/', hash: '#writing' }" exact v-if="!authenticated">
          <span>
            <font-awesome-icon icon="newspaper" />
            <span v-text="t$('globalV1.headers.blog')"></span>
          </span>
        </b-nav-item>
        <b-nav-item href="#newsletter" class="ml-1" exact v-if="!authenticated">
          <span>
            <font-awesome-icon icon="envelopes-bulk" />
            <span v-text="t$('globalV1.headers.newsletter')"></span>
          </span>
        </b-nav-item>
        <b-nav-item-dropdown id="contactUsnavBarDropdown" ref="contactDropdown" class="mx-1">
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="inbox"></font-awesome-icon>
              <span class="no-bold" v-text="t$('globalV1.headers.mailbox')"></span>
            </span>
          </template>
          <b-dropdown-form>
            <div class="d-flex justify-content-between">
              <button class="btn btn-white btn-sm ml-2" variant="white" @click="openWhatsApp">
                <svg xmlns="http://www.w3.org/2000/svg" class="mt-n1" width="21" height="21" fill="var(--white)" viewBox="0 0 256 256">
                  <path
                    d="M187.3,159.06A36.09,36.09,0,0,1,152,188a84.09,84.09,0,0,1-84-84A36.09,36.09,0,0,1,96.94,68.7,12,12,0,0,1,110,75.1l11.48,23a12,12,0,0,1-.75,12l-8.52,12.78a44.56,44.56,0,0,0,20.91,20.91l12.78-8.52a12,12,0,0,1,12-.75l23,11.48A12,12,0,0,1,187.3,159.06ZM236,128A108,108,0,0,1,78.77,224.15L46.34,235A20,20,0,0,1,21,209.66l10.81-32.43A108,108,0,1,1,236,128Zm-24,0A84,84,0,1,0,55.27,170.06a12,12,0,0,1,1,9.81l-9.93,29.79,29.79-9.93a12.1,12.1,0,0,1,3.8-.62,12,12,0,0,1,6,1.62A84,84,0,0,0,212,128Z"
                  ></path>
                </svg>
                Whatsapp
              </button>
              <button class="btn btn-white btn-sm mr-5" variant="white" @click="openMailTo">
                <svg xmlns="http://www.w3.org/2000/svg" class="mt-n1" width="21" height="21" fill="var(--white)" viewBox="0 0 256 256">
                  <path
                    d="M124,128a36,36,0,1,0-36,36A36,36,0,0,0,124,128Zm-48,0a12,12,0,1,1,12,12A12,12,0,0,1,76,128Zm148-28H212V32a12,12,0,0,0-12-12H104A12,12,0,0,0,92,32V56H36A20,20,0,0,0,16,76V180a20,20,0,0,0,20,20H68v16a20,20,0,0,0,20,20H216a20,20,0,0,0,20-20V112A12,12,0,0,0,224,100Zm-52.45,68L212,136.54v62.92ZM116,44h72v80.8l-28,21.78V76a20,20,0,0,0-20-20H116ZM40,80h96v96H40ZM92,200h48a20,20,0,0,0,18.28-11.92L189,212H92Z"
                  ></path>
                </svg>
                Mail
              </button>
            </div>
            <p-separator class="mt-3" :label="separatorLabel"></p-separator>
            <message-contact-v1 @saved="onContactSaved"></message-contact-v1>
          </b-dropdown-form>
        </b-nav-item-dropdown>
      </b-navbar-nav>
      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown id="languagesnavBarDropdown" left v-if="languages && Object.keys(languages).length > 1">
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="earth-africa" />
              <span class="d-lg-none" v-text="t$('global.menu.language')"></span>
            </span>
          </template>
          <!--          https://freefrontend.com/css-menu/-->
          <b-dropdown-item
            v-for="(value, key) in languages"
            :key="`lang-${key}`"
            @click="changeLanguage(key)"
            :class="{ active: isActiveLanguage(key) }"
          >
            <img
              alt="flag"
              src="@content/images/flag_placeholder.png"
              :class="`headers-v1__flag headers-v1__flag--${value.code} ml-2`"
              style="width: 24px"
            />
            {{ value.name }}
          </b-dropdown-item>
        </b-nav-item-dropdown>
        <b-nav-item-dropdown id="themesnavBarDropdown" class="mx-1" right v-if="themes && Object.keys(themes).length > 1">
          <template #button-content>
            <span class="navbar-dropdown-menu">
              <font-awesome-icon icon="circle-half-stroke" />
              <span class="no-bold d-lg-none" v-text="t$('globalV1.headers.theme')"></span>
            </span>
          </template>
          <b-dropdown-item
            v-for="(value, key) in themes"
            :key="`theme-${key}`"
            @click="changeTheme(key)"
            :class="{ active: isActiveTheme(key) }"
          >
            <font-awesome-icon :icon="value.icon" :class="['headers-v1__theme-icon', 'headers-v1__theme-icon--' + key, 'ml-2']" />
            {{ t$(value.nameKey) }}
          </b-dropdown-item>
        </b-nav-item-dropdown>
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
          <b-dropdown-item data-cy="register" to="/register" id="register" v-if="authenticated" active-class="active">
            <font-awesome-icon icon="user-plus" />
            <span v-text="t$('global.menu.account.register')"></span>
          </b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script lang="ts" src="./headers-v1.component.ts"></script>

<style lang="scss" scoped>
.headers-v1 {
  &__brand {
    padding: 0.3rem 0.5rem calc(15px - 0.75rem) !important;
  }

  @media screen and (min-width: 768px) {
    &__toggler {
      display: none;
    }
  }

  @media screen and (min-width: 768px) and (max-width: 1150px) {
    span {
      span {
        display: none;
      }
    }
  }

  .navbar-title {
    display: inline-block;
  }

  /* ==========================================================================
      Logo styles
      ========================================================================== */
  &__logo {
    padding: 0 7px;
  }

  &__title {
    margin-top: -0.1em;
  }

  &__flag {
    background: url('/content/images/flags_responsive.png') no-repeat;
    background-size: 100%;
    vertical-align: middle;
  }
  &__flag--fr {
    background-position: 0 29.752066%;
  }
  &__flag--uk {
    background-position: 0 92.561983%;
  }

  &__theme-icon--light {
    color: #b58900;
  }
  &__theme-icon--dark {
    color: #000000;
  }

  &__loader {
    position: relative;
    width: 300px;
    height: 3px;
    background: linear-gradient(to right, transparent, var(--primary), transparent);
    overflow: hidden;
    &::after {
      content: '';
      position: absolute;
      translate: -200px 0;
      width: 150px;
      height: 100%;
      background: linear-gradient(to right, transparent, var(--primary), transparent);
      animation: slide 1s infinite;
    }
  }

  @keyframes slide {
    100% {
      translate: 300px 0;
    }
  }

  @media screen and (min-width: 768px) {
    & :deep(#contactUsnavBarDropdown .dropdown-menu),
    & :deep(#languagesnavBarDropdown .dropdown-menu),
    & :deep(#themesnavBarDropdown .dropdown-menu) {
      left: 50% !important;
      right: auto !important;
      transform: translateX(-50%) !important;
    }
    & :deep(#contactUsnavBarDropdown .dropdown-menu) {
      min-width: 420px;
    }
  }
}
</style>

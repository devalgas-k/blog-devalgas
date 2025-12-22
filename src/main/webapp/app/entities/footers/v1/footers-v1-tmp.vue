<template>
  <footer class="footer-section" v-if="!authenticated">
    <div class="container">
      <div class="footer-content py-4">
        <div class="row">
          <div class="col-xl-3 col-lg-3 mt-0 mt-md-5 mb-5 offset-2 offset-md-0">
            <div class="footer-widget">
              <div class="d-flex flex-column justify-content-center">
                <div class="footer-logo">
                  <dp-title></dp-title>
                </div>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-lg-3 col-md-3">
            <div class="footer-widget">
              <div class="footer-widget-heading text-primary">
                <h3><span v-text="t$('devalgasApp.footersV1.usefulLinks')"></span></h3>
              </div>
              <ul>
                <li>
                  <a @click="navigateTo('/portfolio')"><span v-text="t$('devalgasApp.footersV1.portfolio')"></span></a>
                </li>
                <li>
                  <a href="#contact" class="smooth-scroll text-white"><span v-text="t$('devalgasApp.footersV1.contact')"></span></a>
                </li>
                <li>
                  <a href="#article-heading" class="smooth-scroll text-white"><span v-text="t$('devalgasApp.footersV1.posts')"></span></a>
                </li>
              </ul>
            </div>
          </div>
          <div class="col-xl-3 col-lg-3 col-md-3 mb-30">
            <div class="footer-widget">
              <div class="footer-widget-heading">
                <h3><span v-text="t$('devalgasApp.footersV1.contactInfo')"></span></h3>
              </div>
              <ul>
                <li>
                  <a href="https://maps.google.com/?q=Flic+en+Flac,+Mauritius" target="_blank" class="text-white">
                    <span v-text="t$('devalgasApp.footersV1.address')"></span>
                  </a>
                </li>
                <li>
                  <a @click="openMailTo"><span v-text="t$('devalgasApp.footersV1.mail')"></span></a>
                </li>
                <li>
                  <a @click="openPhoneCall"><span v-text="t$('devalgasApp.footersV1.number')"></span></a>
                </li>
              </ul>
            </div>
          </div>
          <div class="col-xl-3 col-lg-2 col-md-3 mt-4 mt-md-0">
            <div class="footer-widget">
              <div class="footer-widget-heading">
                <h3><span v-text="t$('devalgasApp.footersV1.followUs')"></span></h3>
              </div>
              <div class="footer-social-icon">
                <a @click="openSocialLink('linkedin')"
                  ><svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="#b58900" viewBox="0 0 256 256">
                    <path
                      d="M216,24H40A16,16,0,0,0,24,40V216a16,16,0,0,0,16,16H216a16,16,0,0,0,16-16V40A16,16,0,0,0,216,24Zm0,192H40V40H216V216ZM96,112v64a8,8,0,0,1-16,0V112a8,8,0,0,1,16,0Zm88,28v36a8,8,0,0,1-16,0V140a20,20,0,0,0-40,0v36a8,8,0,0,1-16,0V112a8,8,0,0,1,15.79-1.78A36,36,0,0,1,184,140ZM100,84A12,12,0,1,1,88,72,12,12,0,0,1,100,84Z"
                    ></path></svg
                ></a>
                <a @click="openSocialLink('twitter')"
                  ><svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="#b58900" viewBox="0 0 256 256">
                    <path
                      d="M214.75,211.71l-62.6-98.38,61.77-67.95a8,8,0,0,0-11.84-10.76L143.24,99.34,102.75,35.71A8,8,0,0,0,96,32H48a8,8,0,0,0-6.75,12.3l62.6,98.37-61.77,68a8,8,0,1,0,11.84,10.76l58.84-64.72,40.49,63.63A8,8,0,0,0,160,224h48a8,8,0,0,0,6.75-12.29ZM164.39,208,62.57,48h29L193.43,208Z"
                    ></path></svg
                ></a>
                <a @click="openSocialLink('github')"
                  ><svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="#b58900" viewBox="0 0 256 256">
                    <path
                      d="M208.31,75.68A59.78,59.78,0,0,0,202.93,28,8,8,0,0,0,196,24a59.75,59.75,0,0,0-48,24H124A59.75,59.75,0,0,0,76,24a8,8,0,0,0-6.93,4,59.78,59.78,0,0,0-5.38,47.68A58.14,58.14,0,0,0,56,104v8a56.06,56.06,0,0,0,48.44,55.47A39.8,39.8,0,0,0,96,192v8H72a24,24,0,0,1-24-24A40,40,0,0,0,8,136a8,8,0,0,0,0,16,24,24,0,0,1,24,24,40,40,0,0,0,40,40H96v16a8,8,0,0,0,16,0V192a24,24,0,0,1,48,0v40a8,8,0,0,0,16,0V192a39.8,39.8,0,0,0-8.44-24.53A56.06,56.06,0,0,0,216,112v-8A58.14,58.14,0,0,0,208.31,75.68ZM200,112a40,40,0,0,1-40,40H112a40,40,0,0,1-40-40v-8a41.74,41.74,0,0,1,6.9-22.48A8,8,0,0,0,80,73.83a43.81,43.81,0,0,1,.79-33.58,43.88,43.88,0,0,1,32.32,20.06A8,8,0,0,0,119.82,64h32.35a8,8,0,0,0,6.74-3.69,43.87,43.87,0,0,1,32.32-20.06A43.81,43.81,0,0,1,192,73.83a8.09,8.09,0,0,0,1,7.65A41.72,41.72,0,0,1,200,104Z"
                    ></path></svg
                ></a>
                <a @click="openSocialLink('medium')"
                  ><svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="#b58900" viewBox="0 0 256 256">
                    <path
                      d="M56,136a8,8,0,0,1-8,8H24a8,8,0,0,1,0-16h8V64H24a8,8,0,0,1,0-16H40v0a8,8,0,0,1,6.78,3.74L80,104.91l33.22-53.15A8,8,0,0,1,120,48v0h16a8,8,0,0,1,0,16h-8v64h8a8,8,0,0,1,0,16H112a8,8,0,0,1,0-16V83.89L86.78,124.24a8,8,0,0,1-13.56,0L48,83.89V128A8,8,0,0,1,56,136Zm112-24h64a8,8,0,0,0,0-16H168a8,8,0,0,0,0,16Zm64,16H168a8,8,0,0,0,0,16h64a8,8,0,0,0,0-16Zm0,32H80a8,8,0,0,0,0,16H232a8,8,0,0,0,0-16Zm0,32H80a8,8,0,0,0,0,16H232a8,8,0,0,0,0-16Z"
                    ></path></svg
                ></a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div>
      <banner-footers></banner-footers>
    </div>
    <div class="copyright-area">
      <div class="container">
        <div class="row">
          <div class="col-xl-6 col-lg-6 text-center text-lg-left">
            <div class="copyright-text">
              <p>
                <span v-text="t$('devalgasApp.footersV1.copyright')"></span>
                <a @click="navigateTo('/about')"><span v-text="t$('devalgasApp.footersV1.me')"></span></a>
              </p>
            </div>
          </div>
          <div class="col-xl-6 col-lg-6 d-none d-lg-block text-right">
            <div class="footer-menu">
              <ul>
                <li>
                  <a href="#header-navbar" class="smooth-scroll"><span v-text="t$('devalgasApp.footersV1.home')"></span></a>
                </li>
                <li>
                  <a @click="navigateTo('/terms')"><span v-text="t$('devalgasApp.footersV1.terms')"></span></a>
                </li>
                <li>
                  <a @click="navigateTo('/privacy')"><span v-text="t$('devalgasApp.footersV1.privacy')"></span></a>
                </li>
                <li>
                  <a @click="navigateTo('/policy')"><span v-text="t$('devalgasApp.footersV1.policy')"></span></a>
                </li>
                <li>
                  <a href="#contact"><span v-text="t$('devalgasApp.footersV1.contact')"></span></a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script lang="ts" src="./footers-v1.component.ts"></script>

<style lang="scss" scoped>
.single-cta {
  color: #fff !important;
}
input {
  border: 3px solid var(--primary) !important;
}

ul {
  margin: 0px;
  padding: 0px;
}
.footer-section {
  position: relative;
}
.footer-cta {
  border-bottom: 2px solid var(--primary);
}

.footer-content {
  position: relative;
  z-index: 2;
}

.footer-logo {
  margin-bottom: 5px;
}

.footer-social-icon {
  margin-top: 40px;
}
.footer-social-icon span {
  color: var(--primary);
  display: block;
  font-size: 20px;
  font-weight: 900;
  font-family: 'Poppins', sans-serif;
}
.footer-social-icon a {
  color: var(--primary);
  font-size: 16px;
  margin-right: 20px;
  cursor: pointer;
}
.footer-social-icon i {
  height: 40px;
  width: 40px;
  text-align: center;
  line-height: 38px;
  border-radius: 50%;
}

.copyright-area {
  background: rgba(0, 0, 0, 0.3);
  padding: 25px 0;
}
.copyright-text p {
  margin: 0;
  font-size: 14px;
  color: #878787;
}
.copyright-text p a {
  color: var(--primary);
  cursor: pointer;
}
.footer-menu li {
  display: inline-block;
  margin-left: 20px;
}
.footer-menu li:hover a {
  color: var(--primary);
}
.footer-menu li a {
  font-size: 14px;
  color: #878787;
  cursor: pointer;
}

.footer-widget ul li {
  width: 100% !important;
  color: white !important;
}
.footer-widget ul li a {
  cursor: pointer;
}

.smooth-scroll {
  scroll-behavior: smooth;
}

html {
  scroll-behavior: smooth;
}
</style>

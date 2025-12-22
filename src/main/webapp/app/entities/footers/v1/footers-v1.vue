<template>
  <!-- TODO https://bootstrapbrain.com/component/bootstrap-signup-form-design/ -->
  <section class="py-3 py-md-5 py-xl-8 d-none">
    <div class="container">
      <div class="row">
        <div class="col-12">
          <div class="mb-5">
            <h2 class="display-5 fw-bold text-center">Contact / NewLetter</h2>
            <p class="text-center m-0">Already have an account? <a href="#!" class="link-primary text-decoration-none">Sign in</a></p>
          </div>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-12 col-lg-10 col-xl-8">
          <div class="row gy-5 justify-content-center">
            <div class="col-12 col-lg-5">
              <form action="#!">
                <div class="row gy-3 overflow-hidden">ddd</div>
              </form>
            </div>
            <div class="col-12 col-lg-2 d-flex align-items-center justify-content-center gap-3 flex-lg-column">
              <div class="bg-dark h-100 d-none d-lg-block" style="width: 1px; --bs-bg-opacity: 0.1"></div>
              <div class="bg-dark w-100 d-lg-none" style="height: 1px; --bs-bg-opacity: 0.1"></div>
              <div>or</div>
              <div class="bg-dark h-100 d-none d-lg-block" style="width: 1px; --bs-bg-opacity: 0.1"></div>
              <div class="bg-dark w-100 d-lg-none" style="height: 1px; --bs-bg-opacity: 0.1"></div>
            </div>
            <div class="col-12 col-lg-5 d-flex align-items-center">
              <div class="d-flex gap-3 flex-column w-100">
                <div>sdsd</div>
                <div>sdsd</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <div class="pt-5">
    <footer class="footer-section">
      <div class="container d-none">
        <div class="footer-cta pb-5">
          <div class="d-flex justify-content-between">
            <div class="col-xl-4 col-md-4 mb-30">
              <div class="single-cta">
                <i class="fas fa-map-marker-alt"></i>
                <div class="cta-text">
                  <h4>Find us</h4>
                  <span>1010 Avenue, sw 54321, chandigarh</span>
                </div>
              </div>
            </div>
            <div class="col-xl-4 offset-lg-1 col-md-4 mb-30">
              <div class="single-cta">
                <i class="fas fa-phone"></i>
                <div class="cta-text">
                  <h4>Call us</h4>
                  <span>9876543210 0</span>
                </div>
              </div>
            </div>
            <div class="col-xl-4 ml-md-5 col-md-4 mb-30">
              <div class="single-cta">
                <i class="far fa-envelope-open"></i>
                <div class="cta-text">
                  <h4>Mail us</h4>
                  <span>mail@info.com</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="footer-content pt-5 pb-5">
          <div class="row">
            <div class="col-xl-4 col-lg-4 mb-50">
              <div class="footer-widget">
                <div class="footer-logo">
                  <dp-title></dp-title>
                </div>
                <div class="footer-text">
                  <p>
                    Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod tempor incididuntut consec tetur adipisicing
                    elit,Lorem ipsum dolor sit amet.
                  </p>
                </div>

                <div class="footer-social-icon">
                  <div class="footer-widget-heading">
                    <h3>Follow us</h3>
                  </div>
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
            <div class="col-xl-4 col-lg-4 col-md-6 mb-30">
              <div class="footer-widget">
                <div class="footer-widget-heading">
                  <h3>Useful Links</h3>
                </div>
                <ul>
                  <li><a href="#">Home</a></li>
                  <li><a href="#">about</a></li>
                  <li><a href="#">services</a></li>
                  <li><a href="#">portfolio</a></li>
                  <li><a href="#">Contact</a></li>
                  <li><a href="#">About us</a></li>
                  <li><a href="#">Our Services</a></li>
                  <li><a href="#">Expert Team</a></li>
                  <li><a href="#">Contact us</a></li>
                  <li><a href="#">Latest News</a></li>
                </ul>
              </div>
            </div>
            <div class="col-xl-4 col-lg-4 col-md-6 mb-50">
              <div class="footer-widget">
                <div class="footer-widget-heading">
                  <h3>Subscribe</h3>
                </div>
                <div class="footer-text mb-25">
                  <p>Don’t miss to subscribe to our new feeds, kindly fill the form below.</p>
                </div>
                <div class="subscribe-form">
                  <form action="#">
                    <input type="text" placeholder="Email Address" />
                    <button><font-awesome-icon icon="search" /></button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="copyright-area">
        <div class="container">
          <div class="row">
            <div class="col-xl-6 col-lg-6 text-center text-lg-left">
              <div class="copyright-text">
                <p>Copyright &copy; 2026, All Right Reserved <a href="#">Devalgas</a></p>
              </div>
            </div>
            <div class="col-xl-6 col-lg-6 d-none d-lg-block text-right">
              <div class="d-none footer-menu">
                <ul>
                  <li><a href="#">Home</a></li>
                  <li><a href="#">Terms</a></li>
                  <li><a href="#">Privacy</a></li>
                  <li><a href="#">Policy</a></li>
                  <li><a href="#">Contact</a></li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script lang="ts" src="./footers-v1.component.ts"></script>

<style lang="scss" scoped>
ul {
  margin: 0px;
  padding: 0px;
}
.footer-section {
  position: relative;
}
.footer-cta {
  border-bottom: 1px solid var(--primary);
}
.single-cta i {
  color: var(--primary);
  font-size: 30px;
  float: left;
  margin-top: 8px;
}
.cta-text {
  padding-right: 15px;
  display: inline-block;
}
.cta-text h4 {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 2px;
}
.cta-text span {
  color: #757575;
  font-size: 15px;
}
.footer-content {
  position: relative;
  z-index: 2;
}
.footer-pattern img {
  position: absolute;
  top: 0;
  left: 0;
  height: 330px;
  background-size: cover;
  background-position: 100% 100%;
}
.footer-logo {
  margin-bottom: 30px;
}
.footer-logo img {
  max-width: 200px;
}
.footer-text p {
  margin-bottom: 14px;
  font-size: 14px;
  color: #7e7e7e;
  line-height: 28px;
}
.footer-social-icon span {
  color: #fff;
  display: block;
  font-size: 20px;
  font-weight: 700;
  font-family: 'Poppins', sans-serif;
  margin-bottom: 20px;
}
.footer-social-icon a {
  color: #fff;
  font-size: 16px;
  margin-right: 15px;
}
.footer-social-icon i {
  height: 40px;
  width: 40px;
  text-align: center;
  line-height: 38px;
  border-radius: 50%;
}
.facebook-bg {
  background: #3b5998;
}
.twitter-bg {
  background: #55acee;
}
.google-bg {
  background: var(--primary);
}
.footer-widget-heading h3 {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 40px;
  position: relative;
}
.footer-widget-heading h3::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: -15px;
  height: 2px;
  width: 50px;
  background: var(--primary);
}

.footer-widget ul li {
  display: inline-block;
  float: left;
  width: 50%;
  margin-top: 12px;
}
.footer-widget ul li a:hover {
  color: var(--primary);
}
.footer-widget ul li a {
  color: #878787;
  text-transform: capitalize;
}
.subscribe-form {
  position: relative;
  overflow: hidden;
}
.subscribe-form input {
  width: 100%;
  padding: 13px 28px;
  background: var(--dark);
  border: 2px solid var(--dark);
  border-radius: 4px 0 0 4px;
  color: #fff;
}
.subscribe-form button {
  position: absolute;
  right: 0;
  background: var(--primary);
  padding: 13px 22px;
  border: 1px solid var(--primary);
  top: 0;
  margin-top: 1px;
  border-radius: 0 4px 4px 0;
}
.subscribe-form button i {
  color: #fff;
  font-size: 22px;
  transform: rotate(-6deg);
}
.copyright-area {
  background-color: rgba(0, 0, 0, 0.2);
  padding: 25px 0;
}
.copyright-text p {
  margin: 0;
  font-size: 14px;
  color: #878787;
}
.copyright-text p a {
  color: var(--primary);
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
}
</style>

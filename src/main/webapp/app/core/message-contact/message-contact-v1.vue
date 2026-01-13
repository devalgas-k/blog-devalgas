<template>
  <body>
    <!-- Creative Contact Form -->
    <div class="form-container mt-1">
      <form class="creative-form">
        <div class="mb-1">
          <div class="row g-3">
            <div class="col-md-6">
              <input type="text" class="form-control" placeholder="First name" />
            </div>
            <div class="col-md-6">
              <input type="text" class="form-control" placeholder="Last name" />
            </div>
          </div>
        </div>
        <div class="mb-1">
          <input type="email" class="form-control" placeholder="Email address" />
        </div>
        <div class="mb-1">
          <textarea class="form-control" rows="4" placeholder="Your message"></textarea>
        </div>
        <button type="submit" class="btn">Send message</button>
      </form>
    </div>
  </body>
</template>

<script lang="ts" src="./message-contact-v1.component.ts"></script>

<style lang="scss" scoped>
body {
  max-height: 80vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}

.form-container {
  margin: 0.5rem auto;
  max-width: 480px;
  position: relative;
}

.creative-form {
  border-radius: 16px;
  color: white;
}

.creative-form .form-control {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  border-radius: 8px;
}

.creative-form .form-control:focus {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: none;
}

.creative-form .form-control::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.creative-form .btn {
  background: #ef233c;
  border: none;
  padding: 14px 28px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.form-title {
  font-weight: 600;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.btn {
  border-radius: 8px;
  transition: all 0.2s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

.form-control:focus {
  outline: none;
}

@media (max-width: 576px) {
  .form-container {
    padding: 1.5rem;
  }
}
</style>

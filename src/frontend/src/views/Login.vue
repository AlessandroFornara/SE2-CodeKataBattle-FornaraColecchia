<template>
  <div class="container d-flex align-items-center justify-content-center">

    <form>

      <div class="mb-3">
        <label for="userInput" class="form-label">Username</label>
        <input type="text" class="form-control" id="userInput" v-model="username">
      </div>

      <div class="mb-3">
        <label for="passInput" class="form-label">Password</label>
        <input type="password" class="form-control" id="passInput" v-model="password">
      </div>

      <div class="row text-center">
        <a
            @click.prevent="register()"
            href="#"
        >{{ registration ? "Already registered. Log in" : "New here? Sign up!" }}</a>
      </div>


      <div
          class="mb-3"
          v-if="registration"
      >
        <label for="emailInput" class="form-label" id="emailI">Email</label>
        <input type="email" class="form-control" id="emailInput" v-model="email">
      </div>

      <div
          class="mb-3"
          v-if="registration"
      >
        <label for="nameInput" class="form-label" id="nameI">Name</label>
        <input type="text" class="form-control" id="nameInput" v-model="name">
      </div>

      <div
          class="mb-3"
          v-if="registration"
      >
        <label for="surnameInput" class="form-label" id="surnameI">Surname</label>
        <input type="text" class="form-control" id="surnameInput" v-model="surname">
      </div>

      <div class="row"
        v-if="registration"
      >

        <div class="col">
          <p>STUDENT</p>
        </div>

        <div class="col d-flex justify-content-center">
          <div class="form-check form-switch">
            <input
                class="form-check-input"
                type="checkbox"
                role="switch"
                id="flexSwitchCheckDefault"
                v-model="isEdu"
            >
          </div>
        </div>

        <div class="col d-flex justify-content-center">
          <p>EDUCATOR</p>
        </div>

      </div>

      <button
          v-if="!registration"
          type="submit"
          class="btn btn-primary"
          @click.prevent="submitLogin()"
      >Log in</button>

      <button
          v-if="registration"
          type="submit"
          class="btn btn-primary"
          @click.prevent="submitRegister()"
      >Sign up</button>

      <p
          v-if="errorMessage!==''"
          class="text-danger text-center"
          role="alert"
      >
        {{ this.errorMessage }}
      </p>

      <p
          v-if="successMessage!==''"
          class="text-success text-center"
          role="alert"
      >
        {{ this.successMessage }}
      </p>

    </form>

  </div>
</template>

<script>
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      email: '',
      name: '',
      surname: '',
      roles: ['STUDENT', 'EDUCATOR'],
      role: '',
      isEdu: false,

      registration: false,

      successMessage: '',
      errorMessage: '',

      responseBody: {},
      requestOptions: {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: null
      }
    }
  },
  methods: {
    submitLogin() {
      this.successMessage = '';
      this.requestOptions.body = JSON.stringify({ username: this.username, password: this.password });

      fetch("/api/auth/login", this.requestOptions)
          .then(response => {
            if (response.status === 200) {
              return response.json().then(data => {
                localStorage.setItem("token", data.token);
                localStorage.setItem("username", data.username);
                localStorage.setItem("role", data.role)
                this.errorMessage = '';
                this.$router.push('/dashboard/home');
              });
            } else {
              return response.text().then(error => {
                this.errorMessage = error;
              });
            }
          })
          .catch(error => {
            this.errorMessage = 'There was an error in sending the request';
            console.error('There was an error in sending the request:', error);
          });
    },
    register() {
      this.registration = (!this.registration)
    },
    submitRegister() {

      let role = this.isEdu ? this.roles[1] : this.roles[0];
      this.requestOptions.body = JSON.stringify({
        username: this.username,
        password: this.password,
        email: this.email,
        name: this.name,
        surname: this.surname,
        role: role
      });

      fetch("/api/auth/register", this.requestOptions)
          .then(response => {
            if (response.status === 200) {
              return response.text().then(data => {
                this.successMessage = data;
                this.errorMessage = '';
                this.registration = false;
              });
            } else {
              return response.text().then(error => {
                this.errorMessage = error;
              });
            }
          })
          .catch(error => {
            this.errorMessage = 'There was an error in sending the request';
            console.error('There was an error in sending the request:', error);
          });
    }
  }
}
</script>

<style scoped>
.container {
  justify-content: center;
  align-items: center;
}
form {
  width: 30%;
}
.text-danger {
  margin-top: 10%;
}
.text-success {
  margin-top: 10%;
}
.btn {
  margin-top: 10%;
}
</style>
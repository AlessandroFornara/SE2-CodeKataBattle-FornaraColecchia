<template>
  <div class="container" style="display: flex; flex-direction: column">

    <div class="container mt-5" style="display: flex; flex-direction: row; align-items: baseline">
      <h1>Profile</h1>
      <h4 style="margin-left: 2%; color: darkgreen">{{role}}</h4>
    </div>

    <div class="container" style="display: flex; flex-direction: row; margin-top: 10%">

      <div class="container" style="display: flex; align-items: center">
        <div class="col" style="width: 35%; display: flex; justify-content: center">
          <img src="@/assets/profile-circle-svgrepo-com.svg" width="300" alt="P">
        </div>
        <div class="col" style="margin-left: 10%">
          <p>Username: <strong>{{username}}</strong></p>
          <p>Email: <strong>{{email}}</strong></p>
        </div>
        <div class="col" style="margin-left: 10%">
          <p>Name: <strong>{{name}}</strong></p>
          <p>Surname: <strong>{{surname}}</strong></p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: 'Profile',
  data() {
    return {
      role: '',
      username: '',
      email: '',
      name: '',
      surname: ''
    }
  },
  created() {
    this.fetchINFO()
  },
  methods: {
    fetchINFO() {
      let endpoint = '/api/auth/userInformation';
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.status ===401) this.$router.push('/login');
            return response.json();
          })
          .then(data => {
            this.role = data.role;
            this.username = data.username;
            this.email = data.email;
            this.name = data.name;
            this.surname = data.surname;
          })
    }
  }
}

</script>
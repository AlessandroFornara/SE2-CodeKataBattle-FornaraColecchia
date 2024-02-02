<template>
  <div class="container">

    <h2>My Tournaments</h2>

    <ul class="list-group" style="margin-top: 20%">
      <li
          class="list-group-item"
          v-for="(t, index) in myT"
          :key="index"
          style="display: flex; justify-content: space-between"
      >
        <a
            @click="this.$router.push('/tntINFO/'+t._id)"
        ><strong>{{t._id}}</strong></a>
        <span class="visibility" style="color: dimgray">{{t.visibility}}</span>
      </li>
    </ul>


  </div>
</template>

<script>
export default {
  name: 'MyTournaments',
  data() {
    return {
      myT: []
    }
  },
  created() {
    this.fetchMyT()
  },
  methods: {
    fetchMyT() {
      this.myT = [];
      let role = localStorage.getItem('role');
      let endpoint = role==='EDUCATOR' ? '/api/eduTnt/myTnt' : '/api/studTnt/myTnt';
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }
      fetch(endpoint, requestOptions)
          .then(response => response.json())
          .then(data => {
            let dataArray = Array.from(data);
            dataArray.forEach(t => {
              this.myT.push(t);
            })
          })
    }
  }
}
</script>

<style>
h2 {
  margin-top: 5%;
  margin-left: 5%;
}


</style>
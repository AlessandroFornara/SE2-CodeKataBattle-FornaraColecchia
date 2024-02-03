<template>
  <div class="container">

    <h2>My Battles</h2>

    <ul class="list-group" style="margin-top: 20%">
      <li
          class="list-group-item"
          v-for="(b, index) in myB"
          :key="index"
          style="display: flex; justify-content: space-between"
      >
        <a
            @click.prevent="this.$router.push('/battleINFO/'+b.tntName+'-'+b.name)"
        ><strong>{{b.name}}</strong></a>
        <a class="visibility" style="color: dimgray" @click="$router.push('/tntINFO/'+b.tntName)">{{b.tntName}}</a>
      </li>
    </ul>
  </div>
</template>

<script>
let MYBATTLES = [];
export default {
  name: 'MyBattles',
  MYBATTLES,
  data() {
    return {
      myB: []
    }
  },
  created() {
    this.fetchMyB()
  },
  methods: {
    fetchMyB() {
      this.myB = [];
      MYBATTLES = [];
      let role = localStorage.getItem('role');
      let endpoint = role==='EDUCATOR' ? '/api/eduBattle/myBattles' : '/api/studTeam/myBattles';
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }
      fetch(endpoint, requestOptions)
          .then(response => {
            if (response.ok) return response.json();
            else if (response.status === 401) this.$router.push('/login');
          })
          .then(data => {
            let dataArray = Array.from(data);
            dataArray.forEach(b => {
              let battle = {
                name: b._id.substring(b._id.indexOf('-')+1),
                tntName: b._id.substring(0,b._id.indexOf('-')),
                rd: b.registrationDeadline,
                sd: b.submitDate
              }
              this.myB.push(battle);
              MYBATTLES.push(b._id);
            })
          })
    },
    getMyB() {
      if (MYBATTLES.length===0) this.fetchMyB();
      return MYBATTLES
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
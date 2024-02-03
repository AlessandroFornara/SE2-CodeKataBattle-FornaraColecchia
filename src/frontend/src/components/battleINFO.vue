<template>
  <div class="container" id="body">
    <div class="container" id="info">

      <div class="container" id="header" style="display: flex; justify-content: space-between; margin-bottom: 2%">
        <div style="display: flex; flex-direction: row; align-items: baseline">
          <h2 style="flex: 1; margin-right: 20px; color: darkgreen"><strong>{{battleName}}</strong></h2>
          <span style="flex: 1">{{tntName}}</span>
        </div>
        <div style="display: flex; align-items: center">
          <button class="btn btn-primary"
                  v-if="role==='EDUCATOR'"
                  :disabled="status!=='CONSOLIDATION'"
                  @click="closeConsolidationStage"
          >Close Consolidation Stage</button>
        </div>
      </div>

      <div class="container" id="footer" style="display: flex; flex-direction: column">
        <div class="container mb-3" style="display: flex; flex-direction: row">
          <div class="col">
            <p>Created by: <strong>{{creator}}</strong></p>
            <p>Min members: <strong>{{min}}</strong></p>
            <p>Max members: <strong>{{max}}</strong></p>
          </div>
          <div class="col">
            <p>STATUS: <strong>{{status}}</strong></p>

            <p v-if="status==='ONGOING'">Started on: <strong>{{regDeadline}}</strong></p>
            <p v-else>Register before: <strong>{{regDeadline}}</strong></p>

            <p>Submit before: <strong>{{submit}}</strong></p>
          </div>
        </div>
        <div class="card m-3"
             style="display: flex; height: 20vh"
             v-if="role==='EDUCATOR'"
        >
          <div class="card-header">Description</div>
          <div class="card-body" style="overflow: auto">
            <p>Repository link: <a href="#">{{repo}}</a></p>
            <p>{{description}}</p>
          </div>
        </div>
      </div>

    </div>
    <div class="container m-5" id="rank" style="display: flex; flex-direction: row">
      <div class="col mr-4" style="height: 45vh; max-width: 35%; border-right: 1px solid dimgray;">
        <h4 style="margin-left: 5%">Rank</h4>
        <ul class="list-group mt-5" style="margin-left: 5%; max-height: 20vh; overflow: auto">
          <li class="list-group-item mr-2"
              style="display: flex; justify-content: space-between; max-width: 90%"
              v-for="(team, index) in teams"
              :key="index"
          >
            <div>
              <span>{{(index+1)+'.       '}}</span>
              <a @click.prevent="getToEvaluate(team)"
                 :style="{ color: team.evaluated ? 'green' : 'black' }"
              ><strong>{{ team.name }}</strong></a>
            </div>
            <span :style="{ color: team.evaluated ? 'green' : 'black' }" >{{ team.points }}</span>
          </li>

        </ul>
      </div>
      <div class="col" style="display: flex; flex-direction: column; justify-content: center">
        <div class="jumbotron m-5" style="background-color: lightgreen; border-radius: 0.3rem" v-if="evaluatedTeam.evaluating&&role==='EDUCATOR'">
          <div class="container" style="display: flex; flex-direction: column">
            <div class="container" style="display: flex; justify-content: space-between; align-items: baseline">
              <div  class="container" style="display: flex; flex-direction: row; align-items: baseline">
                <h4 style="margin-top: 4%">{{evaluatedTeam.name}}</h4>
                <a style="margin-left: 10%" href="#">{{evaluatedTeam.link}}</a>
              </div>
              <h5 >{{evaluatedTeam.points}}</h5>
            </div>
            <hr>
            <table class="table">
              <tbody>
              <tr v-for="(stud, index) in evaluatedTeam.members" :key="index">
                <th style="padding-left: 10%">{{(index+1)+'.        '+stud.s}}</th>
                <td >
                  <input type="number" min="0" max="100" v-model="stud.p" style="padding-left: 2%; margin-left: 65%; max-width: 20%">
                </td>
              </tr>
              </tbody>
            </table>
            <hr>
            <div class="container mb-3" style="display: flex; justify-content: space-between">
              <button class="btn btn-secondary" @click.prevent="evaluatedTeam.evaluating=false">Cancel</button>
              <button class="btn btn-primary"
                      @click.prevent="evaluate"
                      :disabled="this.status!=='CONSOLIDATION'"
              >Evaluate</button>
            </div>
          </div>
        </div>

        <p class="text text-danger" style="width: fit-content; align-self: center"
           v-if="this.evaluatedTeam.evaluating&&errorEvaluate"
        >{{errorEvaluate}}</p>

        <div class="card m-5"
             style="display: flex; height: 20vh"
             v-if="role==='STUDENT'"
        >
          <div class="card-header">Description</div>
          <div class="card-body" style="overflow: auto">
            <p>Repository link: <a href="#">{{repo}}</a></p>
            <p>{{description}}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'battleINFO',
  data() {
    return {
      battleName: this.$route.params.name.substring(this.$route.params.name.indexOf('-')+1),
      tntName: this.$route.params.name.substring(0,this.$route.params.name.indexOf('-')),
      creator: '',
      status: '',
      description: '',
      repo: '',
      min: 0,
      max: 0,
      regDeadline: null,
      submit: null,
      end: null,
      role: localStorage.getItem('role'),
      username: localStorage.getItem('username'),
      evaluatedTeam: {
        evaluating: false,
        name: '',
        points: 0,
        members: []
      },
      teams: [],
      errorEvaluate: ''
    }
  },
  created() {
    this.$watch(
        () => this.$route.params,
        () => {
          this.fetchINFO();
        },
        { immediate: true }
    )

  },
  methods: {
    fetchINFO() {
      let endpoint = this.role==='EDUCATOR' ? '/api/eduBattle/battleInfo?name='+this.$route.params.name : '/api/studTeam/battleInfo?name='+this.$route.params.name;
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }

      fetch(endpoint, requestOptions)
          .then(response => {
            if (response.ok) return response.json()
            else if (response.status === 401) throw new Error("Unauthorized");
            else return response.text().then(error => console.log(error))
          })
          .then(data => {
            this.teams = [];
            this.battleName = data.name.substring(data.name.indexOf('-')+1);
            this.creator = data.creator;
            this.description = data.codeKata.description;
            this.repo = data.repositoryLink;
            this.min = data.minPlayers;
            this.max = data.maxPlayers;
            this.regDeadline = this.dateToLocaleString(data.registrationDeadline);
            this.submit = this.dateToLocaleString(data.submitDate);

            for (const t of data.teams) {
              let team = {
                name: t.name,
                points: t.points,
                members: t.members,
                scores: t.scores,
              }
              this.teams.push(team);
            }

            let d = new Date(data.registrationDeadline);
            let s = new Date(data.submitDate);

            let e = null;
            if (data.endDate!==null) {
              e = new Date(data.endDate);
              this.end = this.dateToLocaleString(data.endDate);
            } else e = new Date("3100");

            let now = new Date();
            if (now>e) this.status = 'CLOSED';
            else if (now>s) this.status = 'CONSOLIDATION';
            else if (now>d) this.status = 'ONGOING';
            else this.status = 'REGISTRATION';

            this.setGreen();
          })
          .catch(error => {
            if (error.message === "Unauthorized") this.$router.push('/login');
          })
    },
    evaluate(){
      let endpoint = '/api/eduBattle/evaluate'

      let user = [];
      let pts = [];
      for (const x of this.evaluatedTeam.members) {
        user.push(x.s);
        pts.push(x.p)
      }
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          battleName: this.tntName+'-'+this.battleName,
          usernames: user,
          points: pts
        })
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) {
              this.evaluatedTeam.evaluating = false;
              this.errorEvaluate = '';
              this.fetchINFO();
            }
            else if (response.status === 401) throw new Error("Unauthorized");
            else return response.text().then(error => this.errorEvaluate=error)
          })
          .catch(error => {
            if (error.message === "Unauthorized") this.$router.push('/login');
          })
    },
    closeConsolidationStage() {
      let endpoint = '/api/eduBattle/close';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          battleName: this.tntName+'-'+this.battleName
        })
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) this.$router.push('/tntINFO/'+this.tntName);
            else if (response.status === 401) this.$router.push('/login');
            else return response.text().then(error => console.log(error))
          })
    },
    getToEvaluate(team) {
      this.evaluatedTeam.evaluating = true;
      this.evaluatedTeam.name = team.name;
      this.evaluatedTeam.points = team.points;
      this.evaluatedTeam.members = [];
      for (const stud in team.members) {

        this.evaluatedTeam.members.push({
          s: team.members[stud],
          p: team.scores.at(stud)
        })
      }
    },
    setGreen() {
      for (const t of this.teams){
        if(t.scores.some(el=> el!==t.points)) t.evaluated = true;
      }
    },
    dateToLocaleString(d) {
      let localDate = new Date(d)
      return localDate.toLocaleString('it-IT', {dateStyle: "medium", timeStyle: "short"})
    }
  }
}
</script>

<style scoped>
#body {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
#info {
  height: fit-content;
  min-height: 35vh;
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid dimgray;
}
#rank {
  display: flex;
  flex-flow: row;
  flex-wrap: wrap;
}
</style>
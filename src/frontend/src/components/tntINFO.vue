<template>
  <div class="container" id="body">
    <div class="container" id="info">

      <div class="container" id="header" style="display: flex; justify-content: space-between; margin-bottom: 2%">
        <div style="display: flex; flex-direction: row; align-items: baseline">
          <h2 style="flex: 1; margin-right: 20px; color: darkgreen"><strong>{{tntName}}</strong></h2>
          <span style="flex: 1; color: darkgrey">{{visibility}}</span>
        </div>

        <div style="display: flex; align-items: center">
          <button
              class="btn btn-primary"
              v-if="subButton.present"
              :disabled="subButton.disabled"
              @click="subscribe"
          >Subscribe</button>
          <button
              class="btn btn-primary"
              v-if="closeButton.present"
              :disabled="closeButton.disabled"
              @click="close"
          >Close</button>
        </div>
      </div>

      <div class="container" id="footer" style="display: flex">
        <div class="col">
          <h4>Info</h4>
          <div class="row mt-4">
            <div class="col" style="display: flex; flex-direction: column; justify-content: center">
              <p>Admin: <strong>{{admin}}</strong></p>
              <div>
                <p>Moderators:</p>
                <ul style="list-style: circle">
                  <li v-for="(mod, index) in moderators"
                      :key="index">
                    <strong>{{mod}}</strong>
                  </li>
                </ul>
              </div>
            </div>
            <div class="col" style="display: flex; flex-direction: column; justify-content: center">
              <p>STATUS: <strong>{{status}}</strong></p>
              <p v-if="status==='ONGOING'">Started on: <strong>{{regDeadline}}</strong></p>
              <p v-else>Register before: <strong>{{regDeadline}}</strong></p>
            </div>
          </div>

          <div class="row m-3 d-flex justify-content-between align-items-center border rounded p-3"
               v-if="this.username===this.admin"
          >
            <p style="max-width: fit-content; margin-bottom: 0; margin-left: 5%"><em>Promote to moderator: </em></p>
            <div class="input-group" style="max-width: 50%; margin-right: 10%">
              <input type="text" class="form-control" placeholder="Username" v-model="newModerator">
              <button class="btn btn-outline-secondary"
                      type="button"
                      :disabled="!newModerator"
                      @click.prevent="promote"
              >Promote</button>
            </div>
            <p class="text-danger" v-if="errorPromote">{{errorPromote}}</p>
          </div>

        </div>

        <div class="col" style="max-height: 40vh; max-width: 35%; border-left: 1px solid dimgray;">
          <h4 style="margin-left: 5%">Rank</h4>
          <ul class="list-group" style="margin-left: 5%; max-height: 20vh; overflow: auto">
            <li class="list-group-item"
                style="display: flex; justify-content: space-between"
                v-for="(s, index) in students"
                :key="index"
            >
              <div>
                <span>{{(index+1)+'.       '}}</span>
                <strong>{{ s.name }}</strong>
              </div>
              <span>{{ s.points }}</span>
            </li>

          </ul>
        </div>

      </div>

    </div>
    <div class="container m-5" id="battles">
      <div class="card m-4"
           style="min-width: 27%; max-width: 30%; height: fit-content; background-color: lightgreen"
           v-for="(battle,index) in battles"
           :key="index"
      >
        <div class="card-header">{{(index+1)+'.    '}}<strong>{{battle.name}}</strong></div>
        <div class="card-body m-2" style="display: flex; flex-direction: column; justify-content: space-between">
          <div>
            <p v-if="battle.status==='ONGOING'">Started on: <strong>{{battle.reg}}</strong></p>
            <div v-if="battle.status==='CONSOLIDATION'||battle.status==='CLOSED'">
              <p>Finished on: <strong>{{battle.sub}}</strong></p>
              <p v-if="battle.status==='CONSOLIDATION'"><em>Evaluation in progress</em></p>
              <p v-if="battle.status==='CLOSED'"><em>Closed</em></p>
            </div>
            <div v-if="battle.status==='REGISTRATION'">
              <p>Register before: <strong>{{battle.reg}}</strong></p>
              <p>Members: {{battle.min}} - {{battle.max}}</p>
            </div>

          </div>
          <div style="display: flex">
            <a v-if="battle.subscribed||(role==='EDUCATOR'&&(username===admin||moderators.includes(username)))"
               href="#"
               @click="this.$router.push('/battleINFO/'+this.tntName+'-'+battle.name)"
            >See Info</a>

            <div v-if="battle.status==='REGISTRATION'&&!battle.subscribed&&role==='STUDENT'&&battle.joining" style="display: flex; justify-content: space-between">
              <button class="btn btn-secondary btn-sm" style="flex: 1; max-width: fit-content" @click.prevent="battle.joining=false">Form Team</button>
              <div style="flex: 1; margin-left: 5%">
                <div class="input-group" style="flex: 1">
                  <input type="password" placeholder="Keyword"
                         style="max-width: 50%"
                         v-model="battle.keyword"
                  >
                  <button class="btn btn-secondary btn-sm"
                          @click.prevent="joinTeam(battle)"
                          :disabled="battle.keyword===''"
                  >Join Team</button>
                </div>
              </div>
            </div>

            <div v-if="battle.status==='REGISTRATION'&&!battle.subscribed&&role==='STUDENT'&&!battle.joining" style="display: flex; justify-content: space-between">
              <div style="flex: 1; margin-left: 5%">
                <div class="input-group" style="flex: 1">
                  <button class="btn btn-secondary btn-sm"
                          @click.prevent="formTeam(battle)"
                          :disabled="battle.teamName===''"
                  >Form Team</button>
                  <input type="text" placeholder="Team name"
                         style="max-width: 50%"
                         v-model="battle.teamName"
                  >
                </div>
              </div>
              <button class="btn btn-secondary btn-sm" style="flex: 1; max-width: fit-content" @click.prevent="battle.joining=true">Join Team</button>
            </div>

          </div>
          <div>
            <p
                class="text-warning text-center"
                role="alert"
                v-if="battle.error!==''"
                style="text-wrap: normal"
            >
              {{battle.error}}
            </p>
          </div>
        </div>
      </div>

      <div class="card m-4"
           style="width: 40%; min-height: 50%; margin-bottom: 0"
           v-if="role==='EDUCATOR'&&(username===admin||moderators.includes(username))"
      >
        <div class="card-header">
          <input type="text" placeholder="New battle" v-model="battleCreationDTO.name">
        </div>
        <div class="card-body m-2" style="display: flex; flex-direction: column; justify-content: space-between">
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Description file: </p>
            <input type="file" @change="updateDescription">
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Register before: </p>
            <input type="datetime-local" :min="minDays" v-model="battleCreationDTO.registrationDeadline">
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Submit before: </p>
            <input type="datetime-local" :min="battleCreationDTO.registrationDeadline" v-model="battleCreationDTO.submissionDeadline">
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Members of teams: </p>
            <div style="display: flex; max-width: 10%; align-items: center; margin-right: 16%">
              <input type="number" placeholder="min" style="max-width: 55px"
                     min="1"
                     @change.prevent="battleCreationDTO.minPlayers>battleCreationDTO.maxPlayers ? battleCreationDTO.maxPlayers = battleCreationDTO.minPlayers : null"
                     v-model="battleCreationDTO.minPlayers">
              <input type="number" placeholder="max" style="max-width: 55px"
                     :min="battleCreationDTO.minPlayers"
                     v-model="battleCreationDTO.maxPlayers">
            </div>
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Input file(s): </p>
            <input type="file" @change="updateInput" multiple>
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">Output file(s): </p>
            <input type="file" @change="updateOutput" multiple>
          </div>
          <div class="container align-items-center" style="display: flex; flex-direction: row; justify-content:space-between">
            <p style="display: flex; align-items: center">WORKFLOW config file: </p>
            <input type="file" @change="updateConfig">
          </div>

          <div v-if="errorNewBattle!==''" class="container" style="display: flex; flex-direction: row; justify-content:center">
            <p
                class="text-danger text-center"
                role="alert"
            >
              {{ this.errorNewBattle }}
            </p>
          </div>
          <div v-if="successNewBattle!==''" class="container" style="display: flex; flex-direction: row; justify-content:center">
            <p
                class="text-success text-center"
            >
              {{ this.successNewBattle }}
            </p>
          </div>
          <div class="container" style="display: flex; flex-direction: row; justify-content:right">
            <button class="btn btn-primary mt-3" @click="createBattle">Create battle</button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import myBattles from "@/components/MyBattles.vue";
export default {
  name: 'tntINFO',
  data() {
    return {
      tntName: this.$route.params.name,
      role: localStorage.getItem('role'),
      username: localStorage.getItem('username'),
      minDays: null,
      subButton: {
        present: false,
        disabled: true
      },
      closeButton: {
        present: false,
        disabled: true
      },
      admin: '',
      moderators: [],
      visibility: '',
      regDeadline: ' ',
      status: '',
      newModerator: '',
      students: [],
      battles: [],
      inputFiles: [],
      outputFiles: [],
      descriptionFile: null,
      configFile: null,
      battleCreationDTO: {
        name: '',
        tournamentName: null,
        registrationDeadline: '',
        submissionDeadline: '',
        codeKata: {
          input: [],
          output: [],
          description: '',
          configurationFile: ''
        },
        maxPlayers: 1,
        minPlayers: 1
      },
      errorNewBattle: '',
      successNewBattle: '',
      errorPromote: '',
      statusCODE: 0
    }
  },
  created() {
    this.$watch(
        () => this.$route.params,
        () => {
          this.fetchINFO();
          this.setTopRightButton();
        },
        { immediate: true }
    )
    this.minDays = this.setDisabledDays();
  },
  methods: {
    fetchINFO() {
      let endpoint = this.role==='EDUCATOR' ? ('/api/eduTnt/tntInfo?name='+this.$route.params.name) : ('/api/studTnt/tntInfo?name='+this.$route.params.name)
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) return response.json();
            else if (response.status === 401) throw new Error("Unauthorized");
            throw new Error(response.statusText);
          })
          .then(data => {
            this.tntName = data.name;
            this.admin = data.admin;
            this.moderators = data.moderators;
            this.visibility = data.visibility;
            this.regDeadline = this.dateToLocaleString(data.registrationDeadline);

            if ((new Date(data.registrationDeadline))>(new Date())) this.status = 'REGISTRATION';
            else this.status = 'ONGOING';

            this.students = [];
            for (const [key, value] of Object.entries(data.rank)) {
              this.students.push({
                name: key,
                points: value
              })
            }
            this.fetchBattles();
          })
          .catch(error => {
            if (error.message === "Unauthorized") this.$router.push('/login');
          })


    },
    fetchBattles(){
      let endpoint = this.role==='EDUCATOR' ? '/api/eduBattle/tntBattles?tournamentName='+this.$route.params.name : '/api/studTeam/tntBattles?tournamentName='+this.$route.params.name;
      let requestOptions = {
        method: 'GET',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token')}
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) return response.json();
            else if (response.status === 401) throw new Error("Unauthorized");
            throw new Error(response.statusText);
          })
          .then(data => {
            this.battles = [];
            var myB = myBattles.methods.getMyB();

            for (let b of data) {
              let battle = {
                name: b.name.substring(this.tntName.length+1),
                min: b.minPlayers,
                max: b.maxPlayers,
                reg: this.dateToLocaleString(b.registrationDeadline),
                sub: this.dateToLocaleString(b.submitDate),
                end: null,
                status: null,
                subscribed: false,
                keyword: '',
                teamName: '',
                joining: false,
                error: ''
              }
              if (myB.includes(b.name)) battle.subscribed = true;

              let d = new Date(b.registrationDeadline);
              let s = new Date(b.submitDate);

              let e = null;
              if (b.endDate!==null) {
                e = new Date(b.endDate);
                battle.end = this.dateToLocaleString(b.endDate);
              } else e = new Date("3100");


              let now = new Date();
              if (now>e) battle.status = 'CLOSED';
              else if (now>s) battle.status = 'CONSOLIDATION';
              else if (now>d) battle.status = 'ONGOING';
              else battle.status = 'REGISTRATION';

              this.battles.push(battle);
            }

            this.setTopRightButton();
          })
          .catch(error => {
            if (error.message === "Unauthorized") this.$router.push('/login');
          })
    },
    updateInput(event) {
      this.inputFiles = Array.from(event.target.files);
    },
    updateOutput(event) {
      this.outputFiles = Array.from(event.target.files);
    },
    updateDescription(event) {
      this.descriptionFile = event.target.files[0];
    },
    updateConfig(event) {
      this.configFile = event.target.files[0];
    },
    readFile(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = e => resolve(e.target.result);
        reader.onerror = e => reject(e);
        reader.readAsText(file);
      });
    },
    async createBattle() {
      let inputContents = [];

      for (const file of this.inputFiles) {
        try {
          let content = await this.readFile(file);
          inputContents.push(content);
        } catch (e) {
          this.errorNewBattle = 'Error reading file: '+e;
        }
      }

      let outputContents = [];

      for (const file of this.outputFiles) {
        try {
          let content = await this.readFile(file);
          outputContents.push(content);
        } catch (e) {
          this.errorNewBattle = 'Error reading file: '+e;
        }
      }

      let description = '';

      try {
        description = await this.readFile(this.descriptionFile);
      } catch (e) {
        this.errorNewBattle = 'Error reading file: '+e;
      }

      let config = '';

      try {
        config = await this.readFile(this.configFile);
      } catch (e) {
        this.errorNewBattle = 'Error reading file: '+e;
      }

      this.battleCreationDTO.codeKata.input = inputContents;
      this.battleCreationDTO.codeKata.output = outputContents;
      this.battleCreationDTO.codeKata.description = description;
      this.battleCreationDTO.codeKata.configurationFile = config;
      this.battleCreationDTO.tournamentName = this.tntName;

      let endpoint = '/api/eduBattle/create'
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify(this.battleCreationDTO)
      }

      console.log(requestOptions.body, endpoint)
      fetch(endpoint,requestOptions)
          .then(response => {
            this.statusCODE = response.status;
            return response.text()
          })
          .then(data => {
            if (this.statusCODE === 401) throw new Error("Unauthorized");
            else if (this.statusCODE === 200) {
              this.errorNewBattle = '';
              this.successNewBattle = data;
              this.fetchBattles();
            }
            else {
              this.successNewBattle = '';
              this.errorNewBattle = data;
            }
          })
          .catch(error => {
            if (error.message === "Unauthorized") this.$router.push('/login');
            else {
              this.successNewBattle = '';
              this.errorNewBattle = error;
            }
          })
    },
    subscribe() {
      let endpoint = '/api/studTnt/subscribe'
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          nameOrKeyword: this.tntName,
          tournamentPublic: this.visibility==='PUBLIC'
        })
      }
      console.log(requestOptions)
      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) this.subButton.disabled = true;
            this.fetchINFO();
          })
    },
    joinTeam(b) {
      let endpoint = '/api/studTeam/join';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          keyword: b.keyword,
          battleName: this.tntName+'-'+b.name
        })
        }
      console.log(requestOptions.body,endpoint);
      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) {
              b.subscribed = true;
            }
            else return response.text().then(error => b.error=error);
          })

    },
    formTeam(b) {
      let endpoint = '/api/studTeam/create';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          name: b.teamName,
          battleName: this.tntName+'-'+b.name
        })
      }
      console.log(requestOptions.body,endpoint);
      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) {
              b.subscribed = true;
            }
            else return response.text().then(error => b.error=error);
          })

    },
    promote() {
      let endpoint = '/api/eduTnt/promote';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          name: this.tntName,
          moderator: this.newModerator
        })
      }

      fetch(endpoint,requestOptions)
          .then(response =>{
            if (response.ok) this.fetchINFO();
            else if (response.status === 401) this.$router.push('/login');
            else return response.text().then(error => this.errorPromote = error)
          })

    },
    close(){
      let endpoint = '/api/eduTnt/close';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          name: this.tntName
        })
      }

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.ok) this.$router.push('/dashboard/home');
            else if (response.status === 401) this.$router.push('/login');
            else return response.text().then(error => console.log(error))
          })

    },
    checkIfAllBClosed() {
      let flag = true;
      for (const b of this.battles) {
        if (b.status !== 'CLOSED') flag = false;
      }
      return flag;
    },
    checkIfSubscribed(stud) {
      for (const s of this.students) {
        if (s.name===stud) return true;
      }
      return false;
    },
    setDisabledDays() {
      let now = new Date();
      let year = now.getFullYear();
      let month = (now.getMonth() + 1).toString().padStart(2, '0');
      let day = (now.getDate()+1).toString().padStart(2, '0');
      let hours = now.getHours().toString().padStart(2, '0');
      let minutes = now.getMinutes().toString().padStart(2, '0');

      return `${year}-${month}-${day}T${hours}:${minutes}`;
    },
    setTopRightButton() {
      this.subButton.present = false;
      this.closeButton.present = false;
      this.subButton.disabled = true;
      this.closeButton.disabled = true;
      if (this.role === 'STUDENT') {
        this.closeButton.present = false;
        if (this.visibility !== 'PRIVATE') this.subButton.present = true;
        if (this.status==='REGISTRATION') this.subButton.disabled = this.checkIfSubscribed(this.username);
      }
      if (this.role === 'EDUCATOR') {
        this.subButton.present = false;
        if (this.admin === this.username) {
          this.closeButton.present = true;
          this.closeButton.disabled = !this.checkIfAllBClosed();
        }
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
  max-height: 40vh;
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid dimgray;
}
#battles {
  display: flex;
  flex-flow: row;
  flex-wrap: wrap;
  overflow-y: auto;
}
</style>
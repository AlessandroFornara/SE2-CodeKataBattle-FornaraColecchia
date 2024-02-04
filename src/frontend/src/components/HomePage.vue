<template>

  <div class="container" id="body">
    <div  id="heading" style="display: flex; flex-direction: row">
      <h2>Hi, </h2>
      <h2 style="margin-left: 1%; color: darkgreen"
          @click.prevent="this.$router.push('/dashboard/profile')">
        {{this.username}}
      </h2>
    </div>

    <div class="row">
      <div class="col" id="ongoing">
        <h4>Ongoing Tournaments: </h4>

        <div class="table-container">
        <table class="table">
          <tbody>
            <tr v-for="(t, index) in OngoingTournaments" :key="index">
              <th style=""><a @click="this.$router.push('/tntINFO/'+t._id)">{{t._id}}</a></th>
              <td>Created by <em>{{t.admin}}</em></td>
              <td style="text-align: right">{{'Started on '+t.registrationDeadline}}</td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>

      <div class="col" id="upcoming">
        <h4 style="text-align: right">Upcoming Tournaments: </h4>
        <div class="table-container">
          <table class="table">
            <tbody>
            <tr v-for="(t, index) in UpcomingTournaments" :key="index">
              <th style=""><a @click="this.$router.push('/tntINFO/'+t._id)">{{t._id}}</a></th>
              <td>Created by <em>{{t.admin}}</em></td>
              <td style="text-align: right">{{t.registrationDeadline}}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>

    <div class="row" style="display: flex; height: 15vh; margin-top: 5%; border-top: 1px solid dimgray">
      <div class="row" id="keyword" style="display: flex; justify-content: space-between"
           v-if="this.role==='STUDENT'"
      >
        <div class="row mt-4" style="display: flex; flex-direction: row; justify-content: space-between; align-items: center">
          <h4 style="max-width: fit-content">Enter a private Tournament:</h4>

          <div class="input-group" style="flex: 1; height: 40%; max-width: 40%">
            <input type="text" class="form-control" placeholder="Insert a keyword" v-model="keywordDTO">
            <button class="btn btn-outline-secondary"
                    type="button"
                    :disabled="!keywordDTO"
                    @click.prevent="subscribePrivateTnt()"
            >Submit</button>
          </div>

          <div class="row mt-3" style="display: flex">
            <div class="alert alert-dismissable alert-success mt-3"
                 v-if="successMessage!==''"
            >
              {{successMessage}}
            </div>
            <div class="alert alert-dismissable alert-danger mt-3"
                 v-if="errorMessage!==''"
            >
              {{errorMessage}}
            </div>
          </div>
        </div>

      </div>

      <div class="row" id="createT" style="display: flex; justify-content: space-between"
           v-if="this.role==='EDUCATOR'"
      >
        <div class="col mt-4">
          <div class="row" style="display: flex">
            <h4 style="flex: 1">Create a new tournament:</h4>

            <div class="alert alert-dismissable alert-success mt-3"
                 v-if="successMessage!==''"
            >
              {{successMessage}}
            </div>

            <div class="alert alert-dismissable alert-danger mt-3"
                 v-if="errorMessage!==''"
            >
              {{errorMessage}}
            </div>

          </div>
        </div>
        <div class="col">
          <div class="mt-3" style="flex: 1; height: 20%">
            <input type="text" class="form-control" placeholder="Name" v-model="nameDTO">
          </div>

          <div class="row mt-3" style="display: flex">
            <p style="flex: 1">Registration deadline: </p>
            <input type="datetime-local" style="flex: 1; max-height: 10%" :min="minDays" v-model="regDeadlineDTO">
          </div>

          <div class="row mt-3" style="display: flex">
            <div class="custom-control custom-radio" style="flex: 1">
              <input type="radio" id="radio1" name="visibility" v-model="visibility" value="public">
              <label class="custom-control-label" for="radio1">PUBLIC</label>
            </div>
            <div class="custom-control custom-radio" style="flex: 1">
              <input type="radio" id="radio2" name="visibility" checked v-model="visibility" value="private">
              <label class="custom-control-label" for="radio2">PRIVATE</label>
            </div>
            <button class="btn btn-primary"
                    style="flex: 1; max-width: fit-content"
                    :disabled="!(nameDTO&&regDeadlineDTO)"
                    @click="createT()"
            >Create</button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      username: '',
      role: '',
      minDays: null,
      createdT: false,
      nameDTO: '',
      regDeadlineDTO: null,
      visibility: null,
      keywordDTO: '',
      successMessage: '',
      errorMessage: '',
      OngoingTournaments: [],
      UpcomingTournaments: []
    }
  },
  created() {
    this.username = localStorage.getItem("username");
    this.role = localStorage.getItem("role");
    this.fetchT();
    this.minDays = this.setDisabledDays();
  },
  methods: {
    fetchT() {
      let role = localStorage.getItem('role');
      let endpoint = role==='EDUCATOR' ? '/api/eduTnt/homePageTnt' : '/api/studTnt/homePageTnt';
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
            this.OngoingTournaments = [];
            this.UpcomingTournaments = [];
            let dataArray = Array.from(data);
            dataArray.forEach(t => this.dispatchT(t))
          })

    },
    dispatchT(t) {
      let now = new Date();
      let reg = new Date(t.registrationDeadline);

      t.registrationDeadline = reg.toLocaleString('it-IT', {dateStyle: "medium", timeStyle: "short"});

      if (reg > now)
        this.UpcomingTournaments.push(t);
      else
        this.OngoingTournaments.push(t);
    },
    createT() {
      let endpoint = '/api/eduTnt/create';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          name: this.nameDTO,
          registrationDeadline: new Date(this.regDeadlineDTO),
          publicTournament: this.visibility==='public'

        })
      };

      fetch(endpoint, requestOptions)
          .then(response => {
            if (response.status === 200) {
              return response.text().then(data => {
                this.errorMessage = '';
                if (this.visibility==='public') this.successMessage = data + ': '+ this.nameDTO;
                else this.successMessage = data;
                this.fetchT();
              });
            } else if (response.status === 401) this.$router.push('/login');
            else {
              return response.text().then(error => {
                this.successMessage = '';
                this.errorMessage = error;
              });
            }
          })
          .catch(error => {
            this.errorMessage = 'There was an error in sending the request';
            console.error(this.errorMessage, error);
          });
    },
    subscribePrivateTnt() {
      let endpoint = '/api/studTnt/subscribe';
      let requestOptions = {
        method: 'POST',
        headers: {'Authorization': 'Bearer '+localStorage.getItem('token'), 'Content-Type': 'application/json'},
        body: JSON.stringify({
          nameOrKeyword: this.keywordDTO,
          tournamentPublic: this.visibility==='public'
        })
      };

      fetch(endpoint,requestOptions)
          .then(response => {
            if (response.status === 200) {
              return response.text().then(data => {
                this.errorMessage = '';
                this.successMessage = data;
              });
            }else if (response.status === 401) this.$router.push('/login');
            else {
              return response.text().then(error => {
                this.successMessage = '';
                this.errorMessage = error;
              });
            }
          })
          .catch(error => {
            this.errorMessage = 'There was an error in sending the request';
            console.error(this.errorMessage, error);
          });
    },
    setDisabledDays() {
      let now = new Date();
      let year = now.getFullYear();
      let month = (now.getMonth() + 1).toString().padStart(2, '0');
      let day = now.getDate().toString().padStart(2, '0');
      let hours = now.getHours().toString().padStart(2, '0');
      let minutes = now.getMinutes().toString().padStart(2, '0');

      return `${year}-${month}-${day}T${hours}:${minutes}`;
    }
  }
}
</script>

<style>
#body {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
#heading {
  height: 15vh;
}
#ongoing {
  height: 55vh;
  margin-right: 5%;
}

.table-container {
  margin-top: 5%;
  margin-bottom: 50px;
  max-height: 50vh;
  overflow: auto;
}
</style>
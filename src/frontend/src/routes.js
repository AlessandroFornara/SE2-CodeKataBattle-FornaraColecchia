import {createRouter, createWebHistory} from "vue-router";
import Login from "@/views/Login.vue";
import Dashboard from "@/views/Dashboard.vue";
import HomePage from "@/components/HomePage.vue";
import Profile from "@/components/Profile.vue";
import tntINFO from "@/components/tntINFO.vue";
import battleINFO from "@/components/battleINFO.vue";
import About from "@/components/About.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'LoginSignup',
            component: Login
        },
        {
            path: '/dashboard',
            name: 'Dashboard',
            redirect: '/dashboard/home',
            component: Dashboard,
            children: [
                {
                    path: '/dashboard/home',
                    name: 'HomePage',
                    component: HomePage
                },
                {
                    path: '/dashboard/profile',
                    name: 'Profile',
                    component: Profile
                },
                {
                    path: '/tntINFO/:name',
                    name: 'tntINFO',
                    component: tntINFO
                },
                {
                    path: '/battleINFO/:name',
                    name: 'battleINFO',
                    component: battleINFO
                }
            ]
        },

    ]
})


export default router
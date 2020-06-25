import React, {useContext, useEffect, useState} from "react";
import AuthLayout from "../layouts/AuthLayout";
import Button from "@material-ui/core/Button";
import {Link, useHistory} from "react-router-dom";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Box from "@material-ui/core/Box";
import {AppContext} from "../utils/AppContext/AppContext";
import queryString from 'query-string';
import axios from 'axios';
import GoogleButton from "react-google-button";

export default function LoginPage() {
    const { setUserContext } = useContext(AppContext);

    const [email, setEmail] = useState(null);
    const [password, setPassword] = useState(null);

    const loginHandler = async (token) => {
        try {
            let res = await axios.get('/auth/me', { headers: { 'X-Auth-Token': token}});
            const me = res.data

            const user = {
                firstName: me.forename,
                lastName: me.name,
                email: me.email
            }; // getUser from API call
            setUserContext(token, user);
         setTimeout(() =>   history.push('/'), 1000)
        } catch (e) {
            alert('Wystapil problem');
            console.error(e);
        }
    };

    let history = useHistory();

    useEffect(() => {
       const tryLogin = async () => {
           if (history && history.location && history.location.search) {
               let params = queryString.parse(history.location.search);
               if (params.token) {
                   await loginHandler(params.token);
               }
           }
       };

       tryLogin()
    }, []);


    const loginSubmit = async () => {

        let res = await axios.post('/auth/signin', { email, password });
        const token = res.data.token

        await loginHandler(token)
    };

    return (
        <AuthLayout>
            <h2>LOGIN</h2>

            <Grid container spacing={2}>

              <Box mx={"auto"}>
                  <GoogleButton
                      onClick={() => {
                          window.location.href="http://localhost:9000/api/auth/oauth/google"
                      }}
                  />
              </Box>

                <Grid item xs={12} >

                    <TextField
                        fullWidth
                        label="E-mail"
                        variant="outlined"
                        required
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        label="Password"
                        variant="outlined"
                        type="password"
                        required
                        value={password}
                        onChange={e => setPassword(e.target.value)}/>
                </Grid>
            </Grid>
            <Box display="flex" flexDirection="row" py={3}>
                <Box flexGrow={1}>
                    <Button variant="contained" color="primary" onClick={loginSubmit}>SIGN IN</Button>
                </Box>
                <Box>
                    <Button color="inherit" component={Link} to="/">HOME PAGE</Button>
                    <Button color="inherit" component={Link} to="/register">REGISTER</Button>
                </Box>
            </Box>
        </AuthLayout>
    )
}

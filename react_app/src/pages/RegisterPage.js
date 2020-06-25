import React, {useState} from "react";
import AuthLayout from "../layouts/AuthLayout";
import Button from "@material-ui/core/Button";
import {Link} from "react-router-dom";
import Grid from "@material-ui/core/Grid";
import TextField from "@material-ui/core/TextField/TextField";
import Box from "@material-ui/core/Box";
import axios from 'axios';

export default function RegisterPage() {

    const [email, setEmail] = useState(null);
    const [password, setPassword] = useState(null);
    const [forename, setForename] = useState(null);
    const [name, setName] = useState(null);
    const [backmessage, setBackmessage] = useState(null);

    const sendUser = async () => {
        let params = {
            firstName: forename,
            lastName: name,
            email: email,
            password: password
        }
        try {
            let res = await axios.post('/auth/signup', params)
            let message = res.data.message
            setBackmessage(message)
        } catch (e) {
            console.log(e);
        }
    }

    return (
        <AuthLayout>
            <h2>REGISTER</h2>
            <Box>
                <p>{backmessage}</p>
            </Box>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <TextField fullWidth label="E-mail" variant="outlined" required value={email}
                               onChange={e => setEmail(e.target.value)}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth label="First name" variant="outlined" required value={forename}
                               onChange={f => setForename(f.target.value)}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth id="outlined-basic" label="Last name" variant="outlined" required value={name}
                               onChange={n => setName(n.target.value)}/>
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth id="outlined-basic" label="Password" variant="outlined" required
                               value={password} onChange={p => setPassword(p.target.value)}
                               type="password"/>
                </Grid>
            </Grid>
            <Box display="flex" flexDirection="row" py={3}>
                <Box flexGrow={1}>
                    <Button variant="contained" color="primary" onClick={sendUser}>SIGN UP</Button>
                </Box>
                <Box>
                    <Button color="inherit" component={Link} to="/">HOME PAGE</Button>
                    <Button color="inherit" component={Link} to="/login">LOGIN</Button>
                </Box>
            </Box>
        </AuthLayout>
    )
}

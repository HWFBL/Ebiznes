import React from "react";
import AuthLayout from "../layouts/AuthLayout";
import Button from "@material-ui/core/Button";
import {Link} from "react-router-dom";
import Grid from "@material-ui/core/Grid";
import TextField from "@material-ui/core/TextField/TextField";
import Box from "@material-ui/core/Box";

export default function RegisterPage() {
    return (
        <AuthLayout>
            <h2>REGISTER</h2>
            <Grid container spacing={2}>
                <Grid item xs={12} >
                    <TextField fullWidth id="outlined-basic" label="E-mail" variant="outlined" />
                </Grid>
                <Grid item xs={12} >
                    <TextField fullWidth id="outlined-basic" label="First name" variant="outlined" />
                </Grid>
                <Grid item xs={12} >
                    <TextField fullWidth id="outlined-basic" label="Last name" variant="outlined" />
                </Grid>
                <Grid item xs={12}>
                    <TextField fullWidth id="outlined-basic" label="Password" variant="outlined" type="password"/>
                </Grid>
            </Grid>
            <Box display="flex" flexDirection="row" py={3}>
                <Box flexGrow={1}>
                    <Button variant="contained" color="primary">SIGN UP</Button>
                </Box>
                <Box>
                    <Button color="inherit" component={Link} to="/">HOME PAGE</Button>
                    <Button color="inherit" component={Link} to="/login">LOGIN</Button>
                </Box>
            </Box>
        </AuthLayout>
    )
}
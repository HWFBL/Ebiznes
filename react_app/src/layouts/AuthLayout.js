import React from "react";
import {makeStyles} from "@material-ui/styles";
import Grid from "@material-ui/core/Grid";
import Box from "@material-ui/core/Box";
import Paper from "@material-ui/core/Paper/Paper";

const useStyles = makeStyles(theme => ({
    root: {
        height: "100vh"
    }
}));


export default function AuthLayout({children}) {
    const classes = useStyles();
    return (
        <Box height="100vh">
            <Grid
                className={classes.root}
                container
                direction="row"
                justify="center"
                alignItems="center"
            >
                <Grid item xs={4}>
                    <Paper elevation={3}>
                        <Box px={4} py={2}>
                            {children}
                        </Box>
                    </Paper>
                </Grid>

            </Grid>
        </Box>

    );
}
import React, {useContext} from "react";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import {makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Box from "@material-ui/core/Box";
import {Link, useHistory} from "react-router-dom";
import {AppContext} from "../utils/AppContext/AppContext";
import AddShoppingCartIcon from '@material-ui/icons/AddShoppingCart';



const useStyles = makeStyles((theme) => ({
    container: {
        minHeight: '100vh',
        backgroundColor: theme.palette.background.paper,
        paddingLeft: '0',
        paddingRight: '0'
    },
    root: {
        flexGrow: 1,
    },
    menuButton: {
        marginRight: theme.spacing(2),
    },
    title: {
        flexGrow: 1,
    },
}));


export default function BaseLayout({children}) {
    let history = useHistory();

    const classes = useStyles();
    const {getBasketItems, getUser, setUserContext} = useContext(AppContext);

    console.log(getUser)
    const logoutHandler = () => {
        setUserContext(null, null);
        history.push('/');
    };

    return (
        <Container className={classes.container} maxWidth="lg">
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" className={classes.title}>
                      MójSklep
                    </Typography>
                    <Button color="inherit" component={Link} to="/">Home</Button>
                    <Button color="inherit" component={Link} to="/categories">Kategorie</Button>
                    <Button color="inherit" component={Link} to="/profile">Profile (debug page)</Button>
                    <Button color="inherit" component={Link} to="/basket">Koszyk <AddShoppingCartIcon/> ({getBasketItems.length})</Button>
                    { getUser ? <Button color="inherit" onClick={logoutHandler}>SIGN OUT</Button> : <Button color="inherit" component={Link} to="/login">LOGIN</Button>}
                </Toolbar>
            </AppBar>
            <Box px={3} py={3}>
                {children}
            </Box>
        </Container>
    )
}

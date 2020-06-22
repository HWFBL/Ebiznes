import React, {useContext} from "react";
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import CardActions from "@material-ui/core/CardActions";
import CardHeader from "@material-ui/core/CardHeader";
import IconButton from "@material-ui/core/IconButton";
import AddShoppingCartIcon from '@material-ui/icons/AddShoppingCart';
import {AppContext} from "../../utils/AppContext/AppContext";
import InfoIcon from '@material-ui/icons/Info';
import Button from "@material-ui/core/Button";
import Box from "@material-ui/core/Box";
import {Link as RouterLink } from "react-router-dom";
import Link from "@material-ui/core/Link";

export default function ProductListItem({ id }) {
    const { addProductToBasket, getBasketItems } = useContext(AppContext);

    const isProductInBasket = () => {
        return getBasketItems.includes(id);
    };

    const AddToBasket = () => {
        return (
            <IconButton size="large" disabled={isProductInBasket()} onClick={() => { addProductToBasket(id) }} color="secondary">
                <AddShoppingCartIcon />
            </IconButton>
        )
    };

    return (
        <>
            <Grid item xs={3}>
                <Card>
                    <Link color="inherit" underline='none' component={RouterLink} to={`/product/${id}`}>
                        <CardHeader title={`Product #${id}`} />
                    </Link>

                    <CardContent>
                        <Typography color="textSecondary">
                            Content
                        </Typography>
                    </CardContent>
                    <CardActions>
                        <AddToBasket/>
                    </CardActions>
                </Card>
            </Grid>
        </>
    )
}
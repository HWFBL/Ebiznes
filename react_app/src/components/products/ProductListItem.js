import React, {useContext, useEffect, useState} from "react";
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import CardActions from "@material-ui/core/CardActions";
import CardHeader from "@material-ui/core/CardHeader";
import IconButton from "@material-ui/core/IconButton";
import CardMedia from '@material-ui/core/CardMedia';
import AddShoppingCartIcon from '@material-ui/icons/AddShoppingCart';
import {AppContext} from "../../utils/AppContext/AppContext";
import InfoIcon from '@material-ui/icons/Info';
import Button from "@material-ui/core/Button";
import Box from "@material-ui/core/Box";
import {Link as RouterLink} from "react-router-dom";
import Link from "@material-ui/core/Link";
import axios from 'axios';

export default function ProductListItem({id}) {
    const {addProductToBasket, getBasketItems} = useContext(AppContext);
    const [photoLink, setPhoto] = useState([])
    useEffect(
        () => {
            const getPhoto = async (id) => {
                try {
                    let res = await axios.get(`/photos/${id.photo}`)
                    let photoLink = res.data
                    setPhoto(photoLink)
                    console.log(photoLink)
                } catch (e) {
                    console.log(e)
                }
            };
            getPhoto(id)
        }, [id]

    )

    const isProductInBasket = () => {
        return getBasketItems.includes(id.id);
    };

    const AddToBasket = () => {
        return (
            <IconButton size="large" disabled={isProductInBasket()} onClick={() => {
                addProductToBasket(id.id)
            }} color="secondary">
                <AddShoppingCartIcon/>
            </IconButton>
        )
    };

    return (
        <>
            <Grid item xs={3}>
                <Card>
                    <Link color="inherit" underline='none' component={RouterLink} to={`/product/${id.id}`}>
                        <CardHeader title={`${id.name}`}/>
                    </Link>
                    <CardContent>
                        {/*<pre>{JSON.stringify(photoLink.link)}</pre>*/}
                        <img src={photoLink.link} height="250px" width="100%"  alt=""/>

                    </CardContent>
                    <CardActions>

                      <AddToBasket/>
                     <Typography variant="h4">
                         {id.price} PLN
                     </Typography>

                    </CardActions>

                </Card>

            </Grid>
        </>
    )
}

import React, {useEffect, useState} from "react";
import BaseLayout from "../layouts/BaseLayout";
import {useParams} from "react-router-dom";
import axios from 'axios';
import ProductListItem from "../components/products/ProductListItem";
import Grid from "@material-ui/core/Grid";

export default function CategoryPage() {
    const [categories, setCategory] = useState([]);

    const urlParams = useParams();
    const categoryId = urlParams.id;

    useEffect(() => {
        const getData = async () => {
            try {
                let res = await axios.get(`/products/category/${categoryId}`);
                let category = res.data;
                // category = category.map(c => c)
                setCategory(category);
                console.log(categories)
            } catch (e) {
                alert("Problem");
                console.error(e);
            }
        };

        getData();
    }, [categoryId]);

    return (

<BaseLayout>
    <Grid container spacing={2}>
            {categories.map(p => (
                <ProductListItem id={p} />
            ))}

    </Grid>

</BaseLayout>

    )
}

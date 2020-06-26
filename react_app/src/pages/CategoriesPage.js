import React, {useEffect, useState} from "react";
import BaseLayout from "../layouts/BaseLayout";
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card/Card";
import CardHeader from "@material-ui/core/CardHeader";
import {Link as RouterLink } from "react-router-dom";
import Link from "@material-ui/core/Link";
import axios from "axios";

export default function CategoriesPage() {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        const getData = async () => {
            try {
                let res = await axios.get(`/category`);
                let category = res.data;
                // category = category.map(c => c.name)
                setCategories(category);
            } catch (e) {
                alert("Problem");
                console.error(e);
            }
        };

        getData();
    }, []);
    return (
        <BaseLayout>
            <h2>Kategorie</h2>
            <Grid container spacing={2}>
                {categories.map(c => (
                    <Grid item xs={3}>
                        <Link underline='none' component={RouterLink} to={`/category/${c.id}`}>
                            <Card>
                                <CardHeader title={`#${c.name}`} />

                            </Card>
                        </Link>
                    </Grid>
                ))}
            </Grid>
        </BaseLayout>
    )
}

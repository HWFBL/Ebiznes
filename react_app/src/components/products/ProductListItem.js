import React from "react";
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import CardActions from "@material-ui/core/CardActions";
import Button from "@material-ui/core/Button";
import CardHeader from "@material-ui/core/CardHeader";

export default function ProductListItem() {
    return (
        <>
            <Grid item xs={3}>
                <Card>
                    <CardHeader title="Title" />
                    <CardContent>
                        <Typography color="textSecondary">
                            Content
                        </Typography>
                    </CardContent>
                    <CardActions>
                        <Button size="small">BUY</Button>
                    </CardActions>
                </Card>

            </Grid>
        </>
    )
}
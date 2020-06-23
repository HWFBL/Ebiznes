import axios from 'axios';


export async function getData(link, param = '') {
    let slash = '/'
    if (param == '') {
        slash = ''
    }
    try {
        let res = await axios.get(link + slash + param);
        const body = res.data;
        // setFunction(body);
        return body;
    } catch (e) {
        alert("Problem");
        console.error(e);
    }
}


const URL_BASE = '/api.filme';
const express = require('express');
const app = express();
const morgan = require('morgan');
app.use(morgan('dev'));
const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded( {extended : false} ) );
app.use(bodyParser.json());

// link para inserir no navegador: http://localhost:3000/api.filme
/*
app.use(URL_BASE, (requisicao, resposta, next) => {
    resposta.status(200).send({
        mensagem : 'URL do app funcionou!'
    });
});
*/
const filmeDao = require('./rotas/filmeDao');
app.use(URL_BASE + '/filme-dao', filmeDao);
/*
app.use( (requisicao, resposta, next) => {
    const erro = new Error('Rota não encontrada :(');
    erro.status = 404;
    next(erro);
});
*/
module.exports = app;
const express = require("express");
const router = express.Router();
const url = require("url");
const querystring = require("querystring");
const connection = require("./dbConfig");

// http://localhost:3000/api.filme/filme-dao/getId?id=1
router.get("/getId", (req, res, next) => {
  /*
    const reqUrl = url.parse(req.url);
    const queryParams = querystring.parse(reqUrl.query);
    const param = queryParams.id;
    res.status(200).send({
        mensagem: "Filme do ID: " + param + " encontrado!",
    });
  */
  const id = req.query.id;
  const query =
    "SELECT titulo, genero, ano FROM catalago_filmes WHERE id = " + id;
  connection.query(query, (err, results) => {
    if (err) {
      console.error("Erro ao buscar filmes:", err);
      res.status(500).send({ erro: "Erro ao buscar filmes" });
      return;
    }
    res.status(200).send(results);
  });
});

/**
Fazer o Insert no Postam usando o JSON
{
    "titulo" : "Cinquenta Tons de Cinza",
    "genero" : "Romance",
    "ano" : 2015
}
*/
// http://localhost:3000/api.filme/filme-dao/insert
router.post("/insert", (req, res, next) => {
  /*
    const { titulo , genero , ano } = req.body;
    const filme = { titulo , genero , ano };
    res.status(200).send({
        mensagem : 'Filme inserido com sucesso!',
        filme
    });
    */

  const { titulo, genero, ano } = req.body;
  const query =
    "INSERT INTO catalago_filmes (titulo, genero, ano) VALUES (?, ?, ?)";
  connection.query(query, [titulo, genero, ano], (err, result) => {
    if (err) {
      console.error("Erro ao inserir filme:", err);
      res.status(500).send({ erro: "Erro ao inserir filme" });
      return;
    }
    res.status(201).send({
      mensagem: "Filme inserido com sucesso!",
      id: result.insertId,
    });
  });
});

// http://localhost:3000/api.filme/filme-dao/getAll
router.get("/getAll", (req, res, next) => {
  /*
    res.status(200).send({
        mensagem : 'Todos os filmes encontrados!'
    });
    */
  const query = "SELECT * FROM catalago_filmes";
  connection.query(query, (err, results) => {
    if (err) {
      console.error("Erro ao buscar filmes:", err);
      res.status(500).send({ erro: "Erro ao buscar filmes" });
      return;
    }
    res.status(200).send(results);
  });
});

// http://localhost:3000/api.filme/filme-dao/update/1
router.put("/update/:id", (req, res, next) => {
  const filmeId = req.params.id;
  const { titulo, genero, ano } = req.body;

  const query = "UPDATE catalago_filmes SET titulo = ?, genero = ?, ano = ? WHERE id = ?";
  connection.query(query, [titulo, genero, ano, filmeId], (err, result) => {
    if (err) {
      console.error("Erro ao atualizar filme:", err);
      res.status(500).send({ erro: "Erro ao atualizar filme" });
      return;
    }
    if (result.affectedRows === 0) {
      res.status(404).send({ mensagem: "Filme não encontrado" });
      return;
    }
    res.status(200).send({ mensagem: "Filme atualizado com sucesso!" });
  });
});

router.delete('/delete/:id', (req, res, next) => {
    const filmeId = req.params.id;
  
    const query = 'DELETE FROM catalago_filmes WHERE id = ?';
    connection.query(query, [filmeId], (err, result) => {
      if (err) {
        console.error('Erro ao remover filme:', err);
        res.status(500).send({ erro: 'Erro ao remover filme' });
        return;
      }
      if (result.affectedRows === 0) {
        res.status(404).send({ mensagem: 'Filme não encontrado' });
        return;
      }
      res.status(200).send({ mensagem: 'Filme removido com sucesso!' });
    });
  });

module.exports = router;

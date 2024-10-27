const mysql = require('mysql2');

const connection = mysql.createConnection({
  host: 'localhost', // Endereço do servidor MySQL
  user: 'root', // Usuário do MySQL
  password: 'root', // Senha do MySQL
  database: 'filme' // Nome do banco de dados
});

connection.connect((err) => {
  if (err) {
    console.error('Erro ao conectar ao banco de dados:', err);
    return;
  }
  console.log('Conexão com o banco de dados estabelecida!');   

});

module.exports = connection;
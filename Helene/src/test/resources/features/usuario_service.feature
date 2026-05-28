Feature: Servicio de usuarios
  Como sistema
  Quiero validar la lógica de negocio del servicio de usuarios

  Scenario: Buscar usuario existente por username
    Given existe un usuario con username "celia" y password "123456"
    When se busca el usuario por username "celia"
    Then el usuario es encontrado
    And el username del usuario encontrado es "celia"

  Scenario: Buscar usuario inexistente
    Given no existe ningún usuario con username "fantasma"
    When se busca el usuario por username "fantasma"
    Then el usuario no es encontrado
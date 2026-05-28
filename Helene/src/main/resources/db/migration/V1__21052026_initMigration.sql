CREATE TABLE carrito_items
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    usuario_id  BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT NULL,
    CONSTRAINT pk_carrito_items PRIMARY KEY (id)
);

CREATE TABLE categorias
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id)
);

CREATE TABLE favoritos
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    usuario_id  BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    CONSTRAINT pk_favoritos PRIMARY KEY (id)
);

CREATE TABLE pedido_items
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    pedido_id       BIGINT NOT NULL,
    producto_id     BIGINT NOT NULL,
    cantidad        INT    NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    descuento_aplicado DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    nombre_producto VARCHAR(255) NULL,
    imagen_producto VARCHAR(500) NULL,
    CONSTRAINT pk_pedido_items PRIMARY KEY (id)
);

CREATE TABLE pedidos
(
    id                       BIGINT AUTO_INCREMENT NOT NULL,
    numero_pedido            VARCHAR(255) NOT NULL,
    fecha_creacion           datetime     NOT NULL,
    fecha_entrega            date NULL,
    usuario_id               BIGINT       NOT NULL,
    estado                   VARCHAR(255) NOT NULL,
    metodo_pago              VARCHAR(255) NOT NULL,
    subtotal DOUBLE NOT NULL,
    coste_envio DOUBLE NOT NULL,
    descuento_aplicado DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    notas                    VARCHAR(500) NULL,
    paypal_payment_id        VARCHAR(255) NULL,
    nombre_completo          VARCHAR(100) NOT NULL,
    calle                    VARCHAR(150) NOT NULL,
    numero                   VARCHAR(10)  NOT NULL,
    piso                     VARCHAR(50) NULL,
    ciudad                   VARCHAR(100) NOT NULL,
    codigo_postal            VARCHAR(10)  NOT NULL,
    provincia                VARCHAR(100) NOT NULL,
    pais                     VARCHAR(100) NOT NULL,
    telefono                 VARCHAR(20)  NOT NULL,
    instrucciones_especiales VARCHAR(500) NULL,
    CONSTRAINT pk_pedidos PRIMARY KEY (id)
);

CREATE TABLE productos
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    precio_original DOUBLE NOT NULL,
    precio_final DOUBLE NOT NULL,
    descuento    INT          NOT NULL,
    descripcion  VARCHAR(500) NULL,
    categoria_id BIGINT       NOT NULL,
    imagen_url   VARCHAR(500) NULL,
    CONSTRAINT pk_productos PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE usuarios
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    username       VARCHAR(50)  NOT NULL,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(100) NULL,
    fecha_registro datetime     NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id)
);

CREATE TABLE usuarios_roles
(
    rol_id     BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT pk_usuarios_roles PRIMARY KEY (rol_id, usuario_id)
);

ALTER TABLE categorias
    ADD CONSTRAINT uc_categorias_nombre UNIQUE (nombre);

ALTER TABLE pedidos
    ADD CONSTRAINT uc_pedidos_numeropedido UNIQUE (numero_pedido);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_nombre UNIQUE (nombre);

ALTER TABLE usuarios
    ADD CONSTRAINT uc_usuarios_username UNIQUE (username);

ALTER TABLE carrito_items
    ADD CONSTRAINT FK_CARRITO_ITEMS_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE carrito_items
    ADD CONSTRAINT FK_CARRITO_ITEMS_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE favoritos
    ADD CONSTRAINT FK_FAVORITOS_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE favoritos
    ADD CONSTRAINT FK_FAVORITOS_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE pedidos
    ADD CONSTRAINT FK_PEDIDOS_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE pedido_items
    ADD CONSTRAINT FK_PEDIDO_ITEMS_ON_PEDIDO FOREIGN KEY (pedido_id) REFERENCES pedidos (id);

ALTER TABLE pedido_items
    ADD CONSTRAINT FK_PEDIDO_ITEMS_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE productos
    ADD CONSTRAINT FK_PRODUCTOS_ON_CATEGORIA FOREIGN KEY (categoria_id) REFERENCES categorias (id);

ALTER TABLE usuarios_roles
    ADD CONSTRAINT fk_usurol_on_rol FOREIGN KEY (rol_id) REFERENCES roles (id);

ALTER TABLE usuarios_roles
    ADD CONSTRAINT fk_usurol_on_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id);
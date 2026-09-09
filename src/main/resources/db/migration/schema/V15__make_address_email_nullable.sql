-- address.email был NOT NULL, что не позволяло сохранить адрес вуза, для которого
-- известны город/регион/улица, но не найден официальный email (см. issue #73 и
-- scripts/README.md). Снимаем ограничение, чтобы не приходилось придумывать email
-- или пропускать такие вузы целиком.
ALTER TABLE address ALTER COLUMN email DROP NOT NULL;

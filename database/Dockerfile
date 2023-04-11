FROM postgres:15-alpine

ENV WAL2JSON_TAG=wal2json_2_5

RUN apk add --no-cache --virtual .wal2json-build-deps gcc clang llvm git make musl-dev \
    && git clone -b $WAL2JSON_TAG --single-branch https://github.com/eulerto/wal2json /root/wal2json \
    && (cd /root/wal2json && make && make install) \
    && rm -rf wal2json \
    && apk del .wal2json-build-deps

RUN mkdir -p /docker-entrypoint-initdb.d
RUN wget -q -P /docker-entrypoint-initdb.d 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/1.initialize-database.sql'
RUN wget -q -P /docker-entrypoint-initdb.d 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/2.initialize-database.sql'
RUN wget -q -P /docker-entrypoint-initdb.d 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/3.initialize-database.sql'
RUN wget -q -P /docker-entrypoint-initdb.d 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/4.initialize-database-json.sql'
RUN wget -q -P /docker-entrypoint-initdb.d 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/5.initialize-database-db-id.sql'

CMD ["postgres", "-c", "listen_addresses=*", "-c", "wal_level=logical"]

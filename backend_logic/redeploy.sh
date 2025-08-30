#!/usr/bin/env bash
set -euo pipefail

# DOVE SIAMO: metti questo file dentro backend_logic/ e lancialo da lì
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

# CONFIG
NET="dieti-net"

EUREKA_NAME="eureka-server"
USER_NAME="user-service"
PROP_NAME="property-service"

EUREKA_IMAGE="dietiestates/eureka-server:latest"
USER_IMAGE="dietiestates/user-service:latest"
PROP_IMAGE="dietiestates/property-service:latest"

EUREKA_PORT=8761
USER_PORT=8081
PROP_PORT=8082

EUREKA_ENV=()
USER_ENV=( -e "EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:${EUREKA_PORT}/eureka/" )
PROP_ENV=( -e "EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:${EUREKA_PORT}/eureka/" )

ensure_network() {
  if ! docker network inspect "$NET" >/dev/null 2>&1; then
    echo "➕ Creo network $NET"
    docker network create "$NET"
  fi
}

build_eureka() {
  echo "🛠  Build $EUREKA_IMAGE"
  docker build -t "$EUREKA_IMAGE" -f eureka-server/Dockerfile .
}

build_user() {
  echo "🛠  Build $USER_IMAGE"
  docker build -t "$USER_IMAGE" -f user-service/Dockerfile .
}

build_prop() {
  echo "🛠  Build $PROP_IMAGE"
  docker build -t "$PROP_IMAGE" -f property-service/Dockerfile .
}

run_eureka() {
  ensure_network
  echo "♻️  Restart container $EUREKA_NAME"
  docker rm -f "$EUREKA_NAME" >/dev/null 2>&1 || true
  docker run -d --name "$EUREKA_NAME" \
    --network "$NET" \
    -p ${EUREKA_PORT}:${EUREKA_PORT} \
    "${EUREKA_ENV[@]}" \
    "$EUREKA_IMAGE"
}

run_user() {
  ensure_network
  echo "♻️  Restart container $USER_NAME"
  docker rm -f "$USER_NAME" >/dev/null 2>&1 || true
  docker run -d --name "$USER_NAME" \
    --network "$NET" \
    -p ${USER_PORT}:${USER_PORT} \
    "${USER_ENV[@]}" \
    "$USER_IMAGE"
}

run_prop() {
  ensure_network
  echo "♻️  Restart container $PROP_NAME"
  docker rm -f "$PROP_NAME" >/dev/null 2>&1 || true
  docker run -d --name "$PROP_NAME" \
    --network "$NET" \
    -p ${PROP_PORT}:${PROP_PORT} \
    "${PROP_ENV[@]}" \
    "$PROP_IMAGE"
}

# COMANDI COMPOSTI
build_all() { build_eureka; build_user; build_prop; }
run_all()   { run_eureka; sleep 2; run_prop; sleep 2; run_user; }

redeploy_one() {
  case "$1" in
    eureka) build_eureka; run_eureka;;
    user)   build_user;   run_user;;
    prop|property)  build_prop;   run_prop;;
    all)    build_all;    run_all;;
    *) echo "Target sconosciuto: $1"; exit 1;;
  esac
}

logs() {
  docker logs -f "$1"
}

ps_() {
  docker ps --format '{{.Names}}\t{{.Status}}\t{{.Ports}}'
}

stop_all() {
  docker rm -f "$USER_NAME" "$PROP_NAME" "$EUREKA_NAME" 2>/dev/null || true
}

usage() {
  cat <<EOF
Usage:
  ./redeploy.sh build   [eureka|user|prop|all]
  ./redeploy.sh run     [eureka|user|prop|all]
  ./redeploy.sh redeploy[eureka|user|prop|all]
  ./redeploy.sh logs    <container-name>
  ./redeploy.sh ps
  ./redeploy.sh stop

Esempi:
  ./redeploy.sh redeploy user        # rebuild + restart user-service
  ./redeploy.sh redeploy prop        # rebuild + restart property-service
  ./redeploy.sh redeploy all         # rebuild + restart tutti
  ./redeploy.sh logs user-service    # segui i log dello user-service
  ./redeploy.sh ps                   # vedi stato container
EOF
}

CMD="${1:-}"; TARGET="${2:-}"

case "${CMD:-}" in
  build)
    case "$TARGET" in
      eureka) build_eureka;;
      user)   build_user;;
      prop|property)  build_prop;;
      all|"") build_all;;
      *) usage; exit 1;;
    esac
    ;;
  run)
    case "$TARGET" in
      eureka) run_eureka;;
      user)   run_user;;
      prop|property)  run_prop;;
      all|"") run_all;;
      *) usage; exit 1;;
    esac
    ;;
  redeploy)
    redeploy_one "${TARGET:-all}"
    ;;
  logs)
    [ -n "${TARGET:-}" ] || { usage; exit 1; }
    logs "$TARGET"
    ;;
  ps)
    ps_
    ;;
  stop)
    stop_all
    ;;
  *)
    usage
    ;;
esac

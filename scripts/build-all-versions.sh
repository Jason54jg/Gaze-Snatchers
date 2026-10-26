#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

VERSIONS=(1.21.11)

if [[ "${BUILD_26:-false}" == "true" ]]; then
  VERSIONS+=(
    26.1 26.1.1 26.1.2 26.2 26.3
  )
fi

merge_properties() {
  local mc="$1"
  cat gradle/versions/common.properties gradle/versions/"${mc}.properties" > gradle.properties
}

mkdir -p release

for mc in "${VERSIONS[@]}"; do
  echo "========== Minecraft ${mc} =========="
  merge_properties "$mc"
  if grep -q '^unobfuscated=true' gradle.properties; then
    if [[ -z "${JAVA_HOME:-}" ]] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q 'version "25'; then
      echo "Skip ${mc}: JDK 25 required (set JAVA_HOME). See docs/MINECRAFT-26.md"
      continue
    fi
  fi
  skip_neo=false
  if grep -q '^skip_neoforge=true' gradle.properties; then
    skip_neo=true
    ./gradlew --no-daemon clean :fabric:remapJar
  else
    ./gradlew --no-daemon clean :fabric:remapJar :neoforge:remapJar
  fi
  out="release/${mc}"
  mkdir -p "$out"
  rm -f "$out"/*.jar
  cp fabric/build/libs/gazesnatchers-*+fabric.jar "$out/" 2>/dev/null || \
    cp fabric/build/libs/gazesnatchers-fabric-*.jar "$out/"
  # Normalize names if classifier differs
  for f in "$out"/gazesnatchers*.jar; do
    [[ -f "$f" ]] || continue
    base="$(basename "$f")"
    if [[ "$base" != *"+fabric"* && "$base" == *fabric* ]]; then
      mv "$f" "$out/gazesnatchers-1.0.0-${mc}+fabric.jar" 2>/dev/null || true
    fi
  done
  if [[ "$skip_neo" == false ]]; then
    cp neoforge/build/libs/gazesnatchers-*+neoforge.jar "$out/" 2>/dev/null || \
      cp neoforge/build/libs/gazesnatchers-neoforge-*.jar "$out/"
  fi
  ls -la "$out"
done

if [[ "${BUILD_26:-false}" == "true" ]]; then
  merge_properties "26.3"
else
  merge_properties "1.21.11"
fi
echo "Done. Artifacts under release/<version>/"

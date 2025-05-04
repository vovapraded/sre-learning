# 1. Создаём кластер с проброшенным портом и без Traefik
k3d cluster create sre-cluster --agents 2 -p "8080:80@loadbalancer" --k3s-arg "--disable=traefik@server:0"

# 2. Подключаемся к кластеру
k3d kubeconfig merge sre-cluster --kubeconfig-switch-context

# 3. Устанавливаем Istio в кластер (демо-профиль — подходит для локального k3d)
istioctl install --set profile=demo -y

# 4. Создаём namespace для микросервисов
kubectl create namespace metrics

# 5. Включаем автопроксирование Istio в namespace
kubectl label namespace metrics istio-injection=enabled

# 6. Собираем docker-образ приложения
cd node-service
mvn package
docker build --platform linux/arm64 -t vovapraded/node-service:0.0.1-arm64 .
cd ..
# 7. Импортируем образ в кластер k3d
k3d image import vovapraded/node-service:0.0.1-arm64 -c sre-cluster

# 8. Применяем все YAML-манифесты в namespace "metrics"
kubectl apply -n metrics -f k8s/

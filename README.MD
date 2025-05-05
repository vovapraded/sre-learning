# 1. Создаём кластер с проброшенным портом и без Traefik
k3d cluster create sre-cluster --agents 2 -p "8080:80@loadbalancer" --k3s-arg "--disable=traefik@server:0"
или менее требовательное
k3d cluster create sre-cluster \
--servers 1 \
--agents 0 \
--no-lb \
--k3s-arg "--disable=traefik,metrics-server@server:0"


# 2. Подключаемся к кластеру
k3d kubeconfig merge sre-cluster --kubeconfig-switch-context

# 3. Устанавливаем Istio в кластер (демо-профиль — подходит для локального k3d)
istioctl install --set profile=demo -y

# 4. Создаём namespace для микросервисов
kubectl create namespace metrics
kubectl config set-context --current --namespace=metrics

# 5. Включаем автопроксирование Istio в namespace
kubectl label namespace metrics istio-injection=enabled

# 6. Билдим и деплоим
mvn package -DskipTests
skaffold dev


sleep 2
cluster_hosts=
docker ps -q -f label=redis |
{
  while read x; do
    private_ip=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $x)
    cluster_hosts="$cluster_hosts $private_ip:6379"
  done
  echo $cluster_hosts
}

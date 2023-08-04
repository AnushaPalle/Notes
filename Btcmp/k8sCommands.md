Pods  
Replicasets  
Deployment  
Services  
Networking  
Volumes  
Configmaps and secrets  
Deamonsets   
jobs  
StatefulSets  

kubectl get nodes   
kubectl describe pod pod_name  
kubectl logs pod_name  
kubectl exec pod_name -it -- sh  -> to get inside a container of a pod  

PODS:  
kubectl get pods -o wide -w  
kubectl apply -f file_name.yaml  -> creates the pod
kubectl delete pod pod_name  




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
kubectl exec pod_name -it -- sh  -> to get inside a container of a pod, incase of a single container pod  
kubectl exec pod_name -c container_name -it -- sh  -> incase of multicontainer pod  

PODS:  
kubectl get pods -o wide -w  
kubectl apply -f file_name.yaml  -> creates the pod  
kubectl delete pod pod_name  

REPLICASET:  
kubectl get replicasets  
kubectl get replicasets -o wide -w    -> watch mode  
kubectl describe rs dobby-rs  
kubectl get pods -o wide --show-labels  
kubectl delete rs replicaset_name  



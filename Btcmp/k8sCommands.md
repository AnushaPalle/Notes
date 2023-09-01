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
kubectl apply -f file_name.yaml  -> creates the replicaset   
kubectl delete rs replicaset_name  


SERVICE:  
kubectl get svc  
kubectl get endpoints  
kubectl apply -f file_name.yaml  -> creates the service  
kubectl describe svc service_name  
kubectl describe endpoints end_point_or_service_name  
for NodePort: -> get node IP from : kubectl get nodes -o wide -> http://node-ip:nodePort  
access via DNS: -> DNS is same as service name -> DNS available only inside the pod -> kubectl exec pod_name -it -- sh -> wget -qO "http://dobbysvc:4444/meta"  

DEPLOYMENT:  
kubectl apply -f filename.yaml -> creates the deployment  
re apply after changing the app ( let's say env ) with above command  
kubectl rollout history deployment/dobby -> number of versions deployed
kubectl rollout undo deployment/dobby    
kubectl rollout undo deployment/dobby --to-revision=1    
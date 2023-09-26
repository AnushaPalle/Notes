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

VOLUMES:   
kubectl apply -f filename.yaml  -> creates the object with volume specified  
kubectl exec podName -c containerName -it -- bash  -> get inside container    
mount | grep html  +  tail -f /usr/share/nginx/html/index.html  
tail -f /html/index.html  
kubectl describe pod podName    
kubectl port-forward pod/host-path-podName 8080:80   

VOLUMES PV & PVC:  
kubectl get pv  
kubectl get pvc  
kubectl apply -f filename.yaml  -> creates the pv | pvc  
kubectl delete pvc pvc_name -> deletes pvc   

VOLUMES SC:  
kubectl get sc  
kubectl apply -f filename.yaml  -> creates the pv dynamically when pvc is created  
kubectl cordon nodeName -> scheduling disabled for that node     

CONFIGMAPS & SECRETS:  
kubectl apply -f filename.yaml  -> creates the configmap or secret    
kubectl get configmap -o wide -w  
kubectl describe configmap configmap_name_from_yaml_spec  
kubectl get secret -o wide -w  
kubectl delete secret secret_name  
kubectl delete configmap configmap_name  

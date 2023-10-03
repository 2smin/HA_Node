# HA_Node
서버이중화의 다양한 모드에 대한 테스트 구현

# 중앙 Control Plane Model

### Master Config Server

worker node의 failover, synchronize 등을 담당

### Worker Nodes

external로부터 traffic을 받아 (http protocols) custom logic 처리

1. Active - Active
2. Active - Standby

# 분산 Control Plane Model (masterless)

### Nodes 

** 설계 구현 생각중....

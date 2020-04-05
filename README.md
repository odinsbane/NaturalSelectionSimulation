# NaturalSelectionSimulation
A small simulation of natural selection and emergent phenomina.

## Beast

The Beast class is what evolves over generations. A Beast will live for some time, moving around and
consuming food. Once it has survived for Beast#reproductive_age time it will divide. When it divides, it's
progeny will randomly evolve slightly. The properties that change will affect it's viability.

There is only one implementation of Beast at the moment, that is the Amoeba.

### Life of the Beast

During a Beast's life cycle, it needs to consume food because it spends life each turn. The most basic version of eating
is to sit in place and consume the food produced by the Terrain which depletes the Terrain locally of
food. If a Beast is not consuming it moves around so it can move new locations which are not depleted
of food.


Each time a beast reproduces, the offspring is changed a small amount. The reproductive_age, size, MAX_VELOCITY, and the
number of traits are varied a bit. The consumption rate is calculated based on the size, speed an the number of traits.
So a beast gains an advantage but they consume more.

### Beast Traits

 + land_affinity
 + predator
 + redirect
 + social
 + conservation
 + graze
 + evasive
 + omnivore
 + fast

Land affinity causes the beast to try and stay on a certain type of land. This can prevent them from leaving a good thing.

Predator means they can eat other beasts.

Social means they follow other beasts of their kind.

Conservation means they don't eat too much.

Graze means they can eat while moving.

Evasive means they have a chance to escape predators.

Ominvore means they eat both beasts and terrain.

Fast they travel faster.

Each trait has the same increase in consumption.

## The Terrain

The Terrain is broken into five types.

 + grass 500 20
 + water 1500 45
 + shallow_water 200 70
 + shore 150 30
 + desert 25 1

 The main difference between the terrains is the food production and capacity.
 the first number is the capacity and the second number is the production.

 ## Results

 I made some of these traits and some of them have an obvious advantages or disadvantages.
 The diversity is impressive. I didn't think grazing would be so popular, but it is one
 of the most important ones. Makes sense in some regards.

 The fact the different types of creatures inhabit different areas is cool. Such as the large
 chunk of deep water can get filled with very large slow creatures. Around the edges where the large
 creatures cannot reach, so smaller creatures form.

 The desert gets sporadically populated by a small creature that sweeps across and wipes out the food
 but the area is too sparse for the larger creatures to survive.

 ## Future

 It's somewhat fun to tune some results and then check out the consequences. In the near future
 I think it would be good to use different maps. Currently the map I generate is not
 great, It has some diversity and produces some interesting results but has a poor aesthetic. 
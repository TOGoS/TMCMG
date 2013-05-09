# TODO

## Bugs
- Source location being unhelpfully reported as 1,1, for some errors  
- Attempt to compile recursive functions (e.g. 'fractal') throws
  'circular definition' errors.

## Desireable features
- Custom block color map (copy from TMCMR)
- Output chunks one region at a time, closing region files when
  done to fix 'too many open files' error when writing lots of regions
- Re-implement grassifier; show in preview somehow.
- 'center on player' button in Export Chunks window

## Won't do
* New procedural object populators
  (but allowing populations of user-defined objects may be a good idea)
* Implicit currying in TNL
  (may be possible in v3 using a curry( function, ... ) function)
* 'Drop' trees and other objects into chunks rather than relying on the ground function
  - Won't work for trees!

## Done
* Add function to make selecting based on input easier
  See 'if'
* Add if function (should support if(cond1, then1, cond2, then2, cond3, then3, else) syntax)
  - also added more comparisons, and, and or!
* Optional depth shading (separate from normal shading) in top-down preview
* Fix caching
  (doesn't work across Layers because Data objects get separate IDs
  due to passing through AdaptInDaDa_DaDaDa_Das)
* Write FUSE filesystem to automatically generate chunks.
* Fix race conditions so GenFSServer works
  * Fix trying to generate a chunk multiple times simultaneously
  * Don't use a TNLCompiler from multiple threads
* Tree populator should have some reasonable limit (like 1 tree per 4
  square meters) on density
* Pine trees
* Add hint bar in Chunk Exporter window showing the player's coordinates,
  if they are found in level.dat.
* Show script errors in WorldDesigner UI
* Make MPP display not flash so much, maybe move it to that status bar at the bottom
  - mouvred to share status bar with script errors 
* Better documentation!
  - There's a README with some lengthy explanations of thins, now.
* Figure out how caves should be displayed.
  - for purposes of GroundFunction, layer floor becomes ground height
    where air layers touch surface

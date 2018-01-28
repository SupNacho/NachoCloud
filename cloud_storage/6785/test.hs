sech :: (Floating a) => a -> a
sech = (+10)

applyTwice :: (a -> a) -> a -> a
applyTwice f x = f (f x)

zipWith1 :: (a -> b -> c) -> [a] -> [b] -> [c]
zipWith1 _ [] _ = []
zipWith1 _ _ []= []
zipWith1 f (x:xs) (y:ys) = f x y : zipWith1 f xs ys

flip1 :: (a -> b -> c) -> b -> a ->c
flip1 f x y = f y x

largestDiv :: Integer
largestDiv = head (filter p [100000,99999..])
  where p x = x `mod` 3829 == 0

chain :: Integer -> [Integer]
chain 1 = [1]
chain n
  | even n = n:chain (n `div` 2)
  | odd n = n:chain (n*3 + 1)

sum1 :: (Num a) => [a] -> a
sum1 xs = foldl (\acc x -> acc + x) 0 xs

elem1 :: (Eq a) => a -> [a] -> Bool
elem1 y ys = foldl (\acc x -> if x == y then True else acc) False ys
2160000
sqrtSums :: Int
sqrtSums = length (takeWhile (<1000) (scanl1 (+) (map sqrt [1..])))+1

summa :: [Integer] -> [Integer] -> [Integer]
summa = filter (>=10) (\x y -> x + y)
